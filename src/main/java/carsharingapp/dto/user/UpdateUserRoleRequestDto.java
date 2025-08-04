package carsharingapp.dto.user;

import carsharingapp.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDto {
    @NotNull
    private Role.RoleName role;
}
