package com.anghel.investmenthelper.user.controller;

import com.anghel.investmenthelper.user.model.dto.user.UserDTO;
import com.anghel.investmenthelper.user.model.dto.user.UserInputDTO;
import com.anghel.investmenthelper.user.model.dto.user.UserUpdateDTO;
import com.anghel.investmenthelper.user.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/internal/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userInputDTO));
    }

    @GetMapping("/users/{authUserId}")
    @PreAuthorize("@userAuthorizationService.canAccessUser(#authUserId, authentication)")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long authUserId) {
        return ResponseEntity.ok(userService.getUserByAuthUserId(authUserId));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{authUserId}")
    @PreAuthorize(
            "@userAuthorizationService.canAccessUser(#authUserId, authentication)"
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long authUserId) {
        userService.deactivateUserByAuthUserId(authUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{authUserId}")
    @PreAuthorize("@userAuthorizationService.canAccessUser(#authUserId, authentication)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long authUserId, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUserByAuthUserId(userUpdateDTO, authUserId));
    }
}
