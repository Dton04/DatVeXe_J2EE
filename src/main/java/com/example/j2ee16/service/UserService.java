package com.example.j2ee16.service;

import com.example.j2ee16.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(String email);
}
