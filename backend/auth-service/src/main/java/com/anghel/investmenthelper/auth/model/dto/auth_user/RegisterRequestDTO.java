package com.anghel.investmenthelper.auth.model.dto.auth_user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @Email
    @NotBlank
    @Size(max = 255, message = "Must have maximum 255 characters")
    private String email;

    @NotBlank
    @Size(min = 6, max = 128, message = "Must be between 6 and 128 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s-]+$",
            message = "First name contains invalid characters."
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z\\s-]+$",
            message = "First name contains invalid characters."
    )
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past
    private LocalDate dateOfBirth;
}
