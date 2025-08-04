package carsharingapp.service;

import carsharingapp.dto.user.UserLoginRequestDto;
import carsharingapp.dto.user.UserLoginResponseDto;

public interface AuthService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
