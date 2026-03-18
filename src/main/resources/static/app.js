const storageKeys = {
  accessToken: "access_token",
  refreshToken: "refresh_token",
};

const modalIds = {
  auth: "authModal",
  account: "accountModal",
};

function setHidden(element, hidden) {
  if (!element) return;
  element.classList.toggle("hidden", hidden);
}

function setMessage(element, message, type) {
  if (!element) return;
  if (!message) {
    element.classList.add("hidden");
    element.textContent = "";
    element.classList.remove("message--ok", "message--error");
    return;
  }
  element.classList.remove("hidden");
  element.textContent = message;
  element.classList.toggle("message--ok", type === "ok");
  element.classList.toggle("message--error", type === "error");
}

function getAccessToken() {
  return localStorage.getItem(storageKeys.accessToken) || "";
}

function getRefreshToken() {
  return localStorage.getItem(storageKeys.refreshToken) || "";
}

function setTokens(accessToken, refreshToken) {
  if (accessToken) localStorage.setItem(storageKeys.accessToken, accessToken);
  if (refreshToken) localStorage.setItem(storageKeys.refreshToken, refreshToken);
}

function clearTokens() {
  localStorage.removeItem(storageKeys.accessToken);
  localStorage.removeItem(storageKeys.refreshToken);
}

function openModal(id) {
  const modal = qs(id);
  if (!modal) return;
  modal.classList.remove("hidden");
}

function closeModal(id) {
  const modal = qs(id);
  if (!modal) return;
  modal.classList.add("hidden");
}

function closeAllModals() {
  closeModal(modalIds.auth);
  closeModal(modalIds.account);
}

async function requestJson(path, options) {
  const response = await fetch(path, {
    headers: {
      "Content-Type": "application/json",
      ...(options && options.headers ? options.headers : {}),
    },
    ...options,
  });

  let body = null;
  try {
    body = await response.json();
  } catch (e) {
    body = null;
  }

  if (!response.ok) {
    const message =
      (body && (body.message || body.error || body.errorMessage)) || "Request failed";
    const errorCode = body && body.errorCode ? body.errorCode : "UNKNOWN";
    const error = new Error(message);
    error.status = response.status;
    error.errorCode = errorCode;
    error.body = body;
    throw error;
  }

  return body;
}

async function refreshAccessToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return "";
  const response = await requestJson("/api/v1/auth/refresh", {
    method: "POST",
    body: JSON.stringify({ refresh_token: refreshToken }),
  });
  if (response && response.access_token) {
    setTokens(response.access_token, null);
    return response.access_token;
  }
  return "";
}

async function getProfileWithAutoRefresh() {
  const accessToken = getAccessToken();
  if (!accessToken) return null;
  try {
    return await requestJson("/api/v1/users/profile", {
      method: "GET",
      headers: { Authorization: "Bearer " + accessToken },
    });
  } catch (e) {
    if (e.status === 401 && getRefreshToken()) {
      const newAccessToken = await refreshAccessToken();
      if (!newAccessToken) return null;
      return await requestJson("/api/v1/users/profile", {
        method: "GET",
        headers: { Authorization: "Bearer " + newAccessToken },
      });
    }
    throw e;
  }
}

function qs(id) {
  return document.getElementById(id);
}

function setActiveTab(tab) {
  const tabLogin = qs("tabLogin");
  const tabRegister = qs("tabRegister");
  const loginForm = qs("loginForm");
  const registerForm = qs("registerForm");
  const authMessage = qs("authMessage");

  setMessage(authMessage, "", "");

  const loginActive = tab === "login";
  tabLogin.classList.toggle("tab--active", loginActive);
  tabRegister.classList.toggle("tab--active", !loginActive);
  setHidden(loginForm, !loginActive);
  setHidden(registerForm, loginActive);
}

function renderAuthenticated(profile) {
  const loginBtn = qs("loginBtn");
  const accountBtn = qs("accountBtn");
  const logoutBtn = qs("logoutBtn");

  setHidden(loginBtn, true);
  setHidden(accountBtn, false);
  setHidden(logoutBtn, false);

  qs("profileUserId").textContent = profile.user_id ?? "-";
  qs("profileFullName").textContent = profile.full_name ?? "-";
  qs("profileRole").textContent = profile.role ?? "-";
}

function renderAnonymous() {
  const loginBtn = qs("loginBtn");
  const accountBtn = qs("accountBtn");
  const logoutBtn = qs("logoutBtn");

  setHidden(loginBtn, false);
  setHidden(accountBtn, true);
  setHidden(logoutBtn, true);
}

function initModalEvents() {
  document.querySelectorAll("[data-close]").forEach((element) => {
    element.addEventListener("click", () => closeModal(element.getAttribute("data-close")));
  });

  window.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeAllModals();
  });
}

function setTripType(type) {
  const tabOneWay = qs("tabOneWay");
  const tabRoundTrip = qs("tabRoundTrip");
  const returnDateField = qs("returnDateField");

  tabOneWay.classList.toggle("pill--active", type === "ONE_WAY");
  tabRoundTrip.classList.toggle("pill--active", type === "ROUND_TRIP");
  setHidden(returnDateField, type !== "ROUND_TRIP");
}

async function init() {
  initModalEvents();

  const tabOneWay = qs("tabOneWay");
  const tabRoundTrip = qs("tabRoundTrip");
  const searchForm = qs("searchForm");
  const searchMessage = qs("searchMessage");

  const tabLogin = qs("tabLogin");
  const tabRegister = qs("tabRegister");
  const loginForm = qs("loginForm");
  const registerForm = qs("registerForm");
  const changePasswordForm = qs("changePasswordForm");
  const loginBtn = qs("loginBtn");
  const accountBtn = qs("accountBtn");
  const logoutBtn = qs("logoutBtn");
  const authMessage = qs("authMessage");
  const passwordMessage = qs("passwordMessage");

  let currentTripType = "ONE_WAY";

  setTripType(currentTripType);
  tabOneWay.addEventListener("click", () => {
    currentTripType = "ONE_WAY";
    setTripType(currentTripType);
  });
  tabRoundTrip.addEventListener("click", () => {
    currentTripType = "ROUND_TRIP";
    setTripType(currentTripType);
  });

  searchForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMessage(searchMessage, "", "");
    const formData = new FormData(searchForm);
    const origin = (formData.get("origin") || "").toString().trim();
    const destination = (formData.get("destination") || "").toString().trim();
    const departDate = (formData.get("depart_date") || "").toString();
    const returnDate = (formData.get("return_date") || "").toString();

    if (!origin || !destination || !departDate) {
      setMessage(searchMessage, "Vui lòng nhập điểm đi, điểm đến và ngày đi", "error");
      return;
    }

    if (currentTripType === "ROUND_TRIP" && !returnDate) {
      setMessage(searchMessage, "Vui lòng chọn ngày về", "error");
      return;
    }

    const summary =
      currentTripType === "ROUND_TRIP"
        ? `Đã lưu tìm kiếm: ${origin} → ${destination} (${departDate} - ${returnDate})`
        : `Đã lưu tìm kiếm: ${origin} → ${destination} (${departDate})`;

    setMessage(searchMessage, summary, "ok");
  });

  tabLogin.addEventListener("click", () => setActiveTab("login"));
  tabRegister.addEventListener("click", () => setActiveTab("register"));

  logoutBtn.addEventListener("click", async () => {
    clearTokens();
    setMessage(passwordMessage, "", "");
    closeAllModals();
    renderAnonymous();
    setActiveTab("login");
  });

  loginBtn.addEventListener("click", () => {
    setActiveTab("login");
    openModal(modalIds.auth);
  });

  accountBtn.addEventListener("click", () => {
    openModal(modalIds.account);
  });

  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMessage(authMessage, "", "");
    const formData = new FormData(loginForm);
    const payload = {
      email: (formData.get("email") || "").toString().trim(),
      password: (formData.get("password") || "").toString(),
    };

    try {
      const result = await requestJson("/api/v1/auth/login", {
        method: "POST",
        body: JSON.stringify(payload),
      });
      setTokens(result.access_token, result.refresh_token);
      const profile = await getProfileWithAutoRefresh();
      if (!profile) throw new Error("Không lấy được profile");
      renderAuthenticated(profile);
      closeModal(modalIds.auth);
    } catch (err) {
      setMessage(authMessage, err.message || "Đăng nhập thất bại", "error");
    }
  });

  registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMessage(authMessage, "", "");
    const formData = new FormData(registerForm);
    const payload = {
      email: (formData.get("email") || "").toString().trim(),
      password: (formData.get("password") || "").toString(),
      full_name: (formData.get("full_name") || "").toString().trim(),
    };

    try {
      await requestJson("/api/v1/auth/register", {
        method: "POST",
        body: JSON.stringify(payload),
      });

      const loginResult = await requestJson("/api/v1/auth/login", {
        method: "POST",
        body: JSON.stringify({ email: payload.email, password: payload.password }),
      });
      setTokens(loginResult.access_token, loginResult.refresh_token);
      const profile = await getProfileWithAutoRefresh();
      if (!profile) throw new Error("Không lấy được profile");
      renderAuthenticated(profile);
      closeModal(modalIds.auth);
    } catch (err) {
      setMessage(authMessage, err.message || "Đăng ký thất bại", "error");
    }
  });

  changePasswordForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMessage(passwordMessage, "", "");
    const formData = new FormData(changePasswordForm);
    const payload = {
      old_pass: (formData.get("old_pass") || "").toString(),
      new_pass: (formData.get("new_pass") || "").toString(),
    };

    try {
      const accessToken = getAccessToken();
      if (!accessToken) throw new Error("Bạn chưa đăng nhập");

      const result = await requestJson("/api/v1/auth/change-password", {
        method: "PUT",
        headers: { Authorization: "Bearer " + accessToken },
        body: JSON.stringify(payload),
      });

      setMessage(passwordMessage, result.message || "Success", "ok");
      changePasswordForm.reset();
    } catch (err) {
      setMessage(passwordMessage, err.message || "Đổi mật khẩu thất bại", "error");
    }
  });

  setActiveTab("login");

  try {
    const profile = await getProfileWithAutoRefresh();
    if (profile) {
      renderAuthenticated(profile);
    } else {
      renderAnonymous();
    }
  } catch (err) {
    renderAnonymous();
  }
}

init();
