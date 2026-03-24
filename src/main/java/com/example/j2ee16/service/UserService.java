package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.UpdateUserProfileRequest;
import com.example.j2ee16.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(String email);
    UserProfileResponse updateProfile(String email, UpdateUserProfileRequest request);
}
