package com.anghel.investmenthelper.user.model.dto;

import com.anghel.investmenthelper.user.util.validator.RolePattern;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {

    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String firstName;

    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String lastName;

    @Email
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String email;

    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String password;

    @Past
    private LocalDate dateOfBirth;

    @RolePattern(anyOf = {"ROLE_ADMIN", "ROLE_USER", "role_admin", "role_user"})
    private String role;
}
