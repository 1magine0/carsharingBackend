package com.carsharing.user.service;

import com.carsharing.user.dto.UpdateProfileRequest;
import com.carsharing.user.dto.UserProfileResponse;
import com.carsharing.user.entity.User;

public interface UserService {
    UserProfileResponse getCurrentUser();
    User getCurrentUserEntity();
    UserProfileResponse updateCurrentUser(UpdateProfileRequest request);
}