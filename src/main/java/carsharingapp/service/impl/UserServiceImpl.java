package carsharingapp.service.impl;

import carsharingapp.dto.user.UpdateUserProfileRequestDto;
import carsharingapp.dto.user.UpdateUserRoleRequestDto;
import carsharingapp.dto.user.UserRegistrationRequestDto;
import carsharingapp.dto.user.UserResponseDto;
import carsharingapp.exception.EntityNotFoundException;
import carsharingapp.exception.RegistrationException;
import carsharingapp.mapper.UserMapper;
import carsharingapp.model.Role;
import carsharingapp.model.User;
import carsharingapp.repository.RoleRepository;
import carsharingapp.repository.UserRepository;
import carsharingapp.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Can't register user with email " + request.getEmail());
        }
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role roleUser = roleRepository.findByRoleName(Role.RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Default role "
                        + Role.RoleName.ROLE_CUSTOMER + " not found"));
        user.setRoles(Set.of(roleUser));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id " + id));
        userMapper.updateUserFromDto(requestDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getCurrentUserProfile(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find user by email: " + name));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfileInfo(String name, UpdateUserProfileRequestDto requestDto) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find user by email: " + name));
        userMapper.updateUserFromDto(requestDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
