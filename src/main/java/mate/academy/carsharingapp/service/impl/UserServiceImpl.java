package mate.academy.carsharingapp.service.impl;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingapp.dao.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingapp.dao.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingapp.dao.user.UserRegistrationRequestDto;
import mate.academy.carsharingapp.dao.user.UserResponseDto;
import mate.academy.carsharingapp.exception.EntityNotFoundException;
import mate.academy.carsharingapp.exception.RegistrationException;
import mate.academy.carsharingapp.mapper.UserMapper;
import mate.academy.carsharingapp.model.Role;
import mate.academy.carsharingapp.model.User;
import mate.academy.carsharingapp.repository.RoleRepository;
import mate.academy.carsharingapp.repository.UserRepository;
import mate.academy.carsharingapp.service.UserService;
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
