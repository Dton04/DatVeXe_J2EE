package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.response.UserProfileResponse;
import com.example.j2ee16.entity.User;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.UserRepository;
import com.example.j2ee16.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(
                        ErrorCodeConstants.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (user.isLocked()) {
            throw new ApiException(ErrorCodeConstants.USER_LOCKED, HttpStatus.FORBIDDEN, "User is locked");
        }

        return new UserProfileResponse(user.getId(), user.getFullName(), user.getRole().name());
    }
}
