package com.juansecu.restart.auth.enums;

public enum EResendVerificationEmailError {
    TOO_MANY_ATTEMPTS("TOO_MANY_ATTEMPTS"),
    USER_NOT_FOUND("USER_NOT_FOUND");

    final String error;

    EResendVerificationEmailError(final String error) {
        this.error = error;
    }
}
