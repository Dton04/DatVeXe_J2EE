package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.CreateStaffRequest;
import com.example.j2ee16.dto.request.UpdateUserLockRequest;
import com.example.j2ee16.dto.response.CreateStaffResponse;
import com.example.j2ee16.dto.response.StaffResponse;
import com.example.j2ee16.entity.User;
import com.example.j2ee16.entity.UserRole;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.UserRepository;
import com.example.j2ee16.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CreateStaffResponse createStaff(CreateStaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(
                    ErrorCodeConstants.EMAIL_ALREADY_EXISTS,
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLocked(false);

        User savedUser = userRepository.save(user);
        return new CreateStaffResponse(savedUser.getId(), "Created");
    }

    @Override
    public List<StaffResponse> getAllStaff() {
        // Based on the prompt, "nhân viên" usually means STAFF and maybe DRIVER. 
        // Example shows role STAFF. I'll include both STAFF and DRIVER to be safe, 
        // or just return all non-ADMIN/non-CUSTOMER users?
        // Let's filter by STAFF and DRIVER roles.
        List<UserRole> staffRoles = Arrays.asList(UserRole.STAFF, UserRole.DRIVER);
        
        return userRepository.findAll().stream()
                .filter(user -> staffRoles.contains(user.getRole()))
                .map(user -> new StaffResponse(user.getId(), user.getEmail(), user.isLocked()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserLock(Long id, UpdateUserLockRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCodeConstants.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        user.setLocked(request.getLocked());
        userRepository.save(user);
    }
}
