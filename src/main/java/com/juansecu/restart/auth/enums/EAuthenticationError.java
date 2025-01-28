package com.juansecu.restart.auth.enums;

public enum EAuthenticationError {
    INVALID_AUTHENTICATION("INVALID_AUTHENTICATION");

    final String errorCode;

    EAuthenticationError(final String errorCode) {
        this.errorCode = errorCode;
    }
}
