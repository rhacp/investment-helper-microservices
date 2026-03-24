package com.anghel.investmenthelper.user.model.dto;

import com.anghel.investmenthelper.user.util.validator.RolePattern;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInputDTO {

    @NotBlank
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String lastName;

    @NotBlank
    @Email
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String email;

    @NotBlank
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String password;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotNull
    @RolePattern(anyOf = {"ROLE_ADMIN", "ROLE_USER", "role_admin", "role_user"})
    private String role;
}
