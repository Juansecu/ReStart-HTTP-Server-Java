package com.juansecu.restart.auth.enums;

public enum ERegisterError {
    INVALID_BIRTHDATE("INVALID_BIRTHDATE"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS"),
    USER_TOO_YOUNG("USER_TOO_YOUNG");

    final String error;

    ERegisterError(final String error) {
        this.error = error;
    }
}
