package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.user.UpdateUserProfileRequestDto;
import carsharingapp.dto.user.UpdateUserRoleRequestDto;
import carsharingapp.dto.user.UserRegistrationRequestDto;
import carsharingapp.dto.user.UserResponseDto;
import carsharingapp.model.Role;
import carsharingapp.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto request);

    void updateUserFromDto(UpdateUserRoleRequestDto request, @MappingTarget User user);

    void updateUserFromDto(UpdateUserProfileRequestDto requestDto, @MappingTarget User user);

    default Set<Role> map(Set<String> roles) {
        return roles.stream()
                .map(name -> {
                    Role role = new Role();
                    try {
                        role.setRoleName(Role.RoleName.valueOf(name));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Unknown role: " + name);
                    }
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
