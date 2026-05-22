package com.anghel.investmenthelper.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserResponseDTO {

    private Long id;

    private String email;

    private String role;

    private boolean enabled;
}
