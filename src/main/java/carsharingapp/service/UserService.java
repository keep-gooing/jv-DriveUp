package carsharingapp.service;

import carsharingapp.dto.user.UpdateUserProfileRequestDto;
import carsharingapp.dto.user.UpdateUserRoleRequestDto;
import carsharingapp.dto.user.UserRegistrationRequestDto;
import carsharingapp.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);

    UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto);

    UserResponseDto getCurrentUserProfile(String name);

    UserResponseDto updateProfileInfo(String name, UpdateUserProfileRequestDto requestDto);
}
