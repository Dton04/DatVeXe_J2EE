package com.example.j2ee16.controller;

import com.example.j2ee16.dto.request.CreateStaffRequest;
import com.example.j2ee16.dto.request.UpdateUserLockRequest;
import com.example.j2ee16.dto.response.CreateStaffResponse;
import com.example.j2ee16.dto.response.StaffResponse;
import com.example.j2ee16.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping
    public ResponseEntity<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserService.createStaff(request));
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(adminUserService.getAllStaff());
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<Map<String, String>> updateUserLock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserLockRequest request
    ) {
        adminUserService.updateUserLock(id, request);
        return ResponseEntity.ok(Collections.singletonMap("message", "User status updated"));
    }
}
