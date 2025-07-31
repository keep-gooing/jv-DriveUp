package mate.academy.carsharingapp.service;

import mate.academy.carsharingapp.dao.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingapp.dao.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingapp.dao.user.UserRegistrationRequestDto;
import mate.academy.carsharingapp.dao.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);

    UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto);

    UserResponseDto getCurrentUserProfile(String name);

    UserResponseDto updateProfileInfo(String name, UpdateUserProfileRequestDto requestDto);
}
