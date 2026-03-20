package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.CreateStaffRequest;
import com.example.j2ee16.dto.request.UpdateUserLockRequest;
import com.example.j2ee16.dto.response.CreateStaffResponse;
import com.example.j2ee16.dto.response.StaffResponse;

import java.util.List;

public interface AdminUserService {
    CreateStaffResponse createStaff(CreateStaffRequest request);
    List<StaffResponse> getAllStaff();
    void updateUserLock(Long id, UpdateUserLockRequest request);
}
