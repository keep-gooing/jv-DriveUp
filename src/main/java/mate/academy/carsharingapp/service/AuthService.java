package mate.academy.carsharingapp.service;

import mate.academy.carsharingapp.dao.user.UserLoginRequestDto;
import mate.academy.carsharingapp.dao.user.UserLoginResponseDto;

public interface AuthService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
