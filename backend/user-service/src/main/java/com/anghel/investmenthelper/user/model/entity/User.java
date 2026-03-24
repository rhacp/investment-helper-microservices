package com.anghel.investmenthelper.user.model.entity;

import com.anghel.investmenthelper.user.util.enumeration.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName",  nullable = false)
    private String lastName;

    @Column(name = "email",  nullable = false, unique = true)
    private String email;

    @Column(name = "passwordHash",  nullable = false)
    private String passwordHash;

    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate;

    @Column(name = "creationTime", nullable = false)
    private LocalTime creationTime;

    @Column(name = "dateOfBirth",  nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "role",  nullable = false)
    private Role role;
}
