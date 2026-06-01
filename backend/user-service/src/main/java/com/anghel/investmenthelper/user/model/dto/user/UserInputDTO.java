package com.anghel.investmenthelper.user.model.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDTO {

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

    @NotNull(message = "UserAuth id is required")
    private Long authUserId;
}
