package com.anghel.investmenthelper.auth.model.dto.auth_user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;
}
