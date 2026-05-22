package com.anghel.investmenthelper.user.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @Pattern(
            regexp = "^[A-Za-z\\s-]+$",
            message = "First name contains invalid characters."
    )
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String firstName;

    @Pattern(
            regexp = "^[A-Za-z\\s-]+$",
            message = "First name contains invalid characters."
    )
    @Size(min = 3, max = 30, message = "Must be between 3 and 30 characters.")
    private String lastName;

    @Past
    private LocalDate dateOfBirth;
}
