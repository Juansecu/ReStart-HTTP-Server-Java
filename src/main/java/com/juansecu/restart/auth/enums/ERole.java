package com.juansecu.restart.auth.enums;

import lombok.Getter;

@Getter
public enum ERole {
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER");

    final String role;

    ERole(final String role) {
        this.role = role;
    }
}
