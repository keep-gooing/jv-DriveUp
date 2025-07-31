package mate.academy.carsharingapp.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.carsharingapp.config.MapperConfig;
import mate.academy.carsharingapp.dao.user.UpdateUserProfileRequestDto;
import mate.academy.carsharingapp.dao.user.UpdateUserRoleRequestDto;
import mate.academy.carsharingapp.dao.user.UserRegistrationRequestDto;
import mate.academy.carsharingapp.dao.user.UserResponseDto;
import mate.academy.carsharingapp.model.Role;
import mate.academy.carsharingapp.model.User;
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
