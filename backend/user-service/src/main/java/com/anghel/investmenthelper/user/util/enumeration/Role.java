package com.anghel.investmenthelper.user.util.enumeration;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String label;

    Role(String label) {
        this.label = label;
    }
}
