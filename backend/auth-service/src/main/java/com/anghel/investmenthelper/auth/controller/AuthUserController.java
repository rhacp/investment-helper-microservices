package com.anghel.investmenthelper.auth.controller;

import com.anghel.investmenthelper.auth.model.dto.auth_user.*;
import com.anghel.investmenthelper.auth.service.auth_user.AuthUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthUserController {

    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authUserService.register(registerRequestDTO));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authUserService.login(loginRequestDTO));
    }

    @PatchMapping("/auth/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthUserResponseDTO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(authUserService.updateUserRole(id, roleDTO));
    }

    @PatchMapping("/internal/auth-users/{authUserId}/disable")
    public ResponseEntity<Void> disableAuthUser(@PathVariable Long authUserId) {
        authUserService.disableAuthUser(authUserId);
        return ResponseEntity.noContent().build();
    }
}
