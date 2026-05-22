package com.anghel.investmenthelper.auth.model.dto;

import com.anghel.investmenthelper.auth.util.validator.RolePattern;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    @NotNull
    @RolePattern(anyOf = {"ROLE_USER", "ROLE_ADMIN"})
    private String role;
}
