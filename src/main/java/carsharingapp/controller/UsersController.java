package carsharingapp.controller;

import carsharingapp.dto.user.UpdateUserProfileRequestDto;
import carsharingapp.dto.user.UpdateUserRoleRequestDto;
import carsharingapp.dto.user.UserResponseDto;
import carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update user role", description = "Update user role")
    @PutMapping("/{id}/role")
    public UserResponseDto updateUserRole(@PathVariable Long id,
                                  @RequestBody @Valid UpdateUserRoleRequestDto requestDto) {
        return userService.updateUserRole(id, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Get my profile info", description = "Get my profile info")
    @GetMapping("/me")
    public UserResponseDto getCurrentUserProfile(Authentication authentication) {
        return userService.getCurrentUserProfile(authentication.getName());
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Update profile info", description = "Update profile info")
    @PutMapping("/me")
    public UserResponseDto updateProfileInfo(@RequestBody
                                                 @Valid UpdateUserProfileRequestDto requestDto,
                                             Authentication authentication) {
        return userService.updateProfileInfo(authentication.getName(), requestDto);
    }
}
