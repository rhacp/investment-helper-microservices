package com.anghel.investmenthelper.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String passwordHash;

    private LocalDate creationDate;

    private LocalTime creationTime;

    private LocalDate dateOfBirth;

    private String role;
}
