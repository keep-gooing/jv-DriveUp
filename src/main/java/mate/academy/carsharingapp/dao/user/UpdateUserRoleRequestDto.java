package mate.academy.carsharingapp.dao.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.carsharingapp.model.Role;

@Data
public class UpdateUserRoleRequestDto {
    @NotNull
    private Role.RoleName role;
}
