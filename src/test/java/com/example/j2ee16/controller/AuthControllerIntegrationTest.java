package com.example.j2ee16.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginRefreshFlow() throws Exception {
        String registerBody = """
                {
                  "email": "customer1@example.com",
                  "password": "123456",
                  "full_name": "Nguyen Van A"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Register success"))
                .andExpect(jsonPath("$.user_id").isNumber());

        String loginBody = """
                {
                  "email": "customer1@example.com",
                  "password": "123456"
                }
                """;

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.refresh_token").isString())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String refreshToken = loginJson.get("refresh_token").asText();
        assertThat(refreshToken).isNotBlank();

        String refreshBody = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("refresh_token", refreshToken));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString());
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        String registerBody = """
                {
                  "email": "customer2@example.com",
                  "password": "123456",
                  "full_name": "Tran Thi B"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String wrongLoginBody = """
                {
                  "email": "customer2@example.com",
                  "password": "wrongpass"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.httpStatus").value(401));
    }

    @Test
    void changePasswordRequiresAuthentication() throws Exception {
        String body = """
                {
                  "old_pass": "123456",
                  "new_pass": "abcdef"
                }
                """;

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.httpStatus").value(401));
    }

    @Test
    void changePasswordThenLoginWithNewPassword() throws Exception {
        String email = "customer3@example.com";
        String oldPassword = "123456";
        String newPassword = "abcdef";

        register(email, oldPassword, "Le Van C");
        String accessToken = loginAndGetAccessToken(email, oldPassword);

        String body = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("old_pass", oldPassword)
                .put("new_pass", newPassword));

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));

        String oldLoginBody = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("email", email)
                .put("password", oldPassword));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(oldLoginBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.httpStatus").value(401));

        String newLoginBody = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("email", email)
                .put("password", newPassword));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newLoginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.refresh_token").isString())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void profileReturnsCurrentUser() throws Exception {
        String email = "customer4@example.com";
        String password = "123456";
        String fullName = "Pham Thi D";

        register(email, password, fullName);
        String accessToken = loginAndGetAccessToken(email, password);

        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").isNumber())
                .andExpect(jsonPath("$.full_name").value(fullName))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    private void register(String email, String password, String fullName) throws Exception {
        String registerBody = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("email", email)
                .put("password", password)
                .put("full_name", fullName));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        String loginBody = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("email", email)
                .put("password", password));

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        return loginJson.get("access_token").asText();
    }
}
