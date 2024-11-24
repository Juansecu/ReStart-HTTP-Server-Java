package com.juansecu.restart.auth;

public final class AuthenticationValidationMessages {
    public static final String EMAILS_NOT_MATCH = "E-mails do not match";
    public static final String INVALID_BIRTHDATE = "Invalid birthdate format! It must be in yyyy-MM-dd format";
    public static final String INVALID_EMAIL = "Invalid e-mail address";
    public static final String INVALID_EMAIL_CONFIRMATION = "Invalid e-mail confirmation address";
    public static final String INVALID_PASSWORD_CONFIRMATION_LENGTH =
        "Invalid password confirmation length! It must be between " +
            AuthenticationValidations.MIN_PASSWORD_LENGTH +
            " and " +
            AuthenticationValidations.MAX_PASSWORD_LENGTH +
            " characters";
    public static final String INVALID_PASSWORD_LENGTH =
        "Invalid password length! It must be between " +
            AuthenticationValidations.MIN_PASSWORD_LENGTH +
            " and " +
            AuthenticationValidations.MAX_PASSWORD_LENGTH +
            " characters";
    public static final String INVALID_USERNAME =
        "Invalid username. Username can only contain alphanumeric characters, including - and _ chars";
    public static final String INVALID_USERNAME_LENGTH =
        "Invalid username length! It must be between " +
            AuthenticationValidations.MIN_USERNAME_LENGTH +
            " and " +
            AuthenticationValidations.MAX_USERNAME_LENGTH +
            " characters";
    public static final String NOT_EMPTY_BIRTHDATE = "Birthdate cannot be empty";
    public static final String NOT_EMPTY_EMAIL = "E-mail cannot be empty";
    public static final String NOT_EMPTY_EMAIL_CONFIRMATION = "E-mail confirmation cannot be empty";
    public static final String NOT_EMPTY_PASSWORD = "Password cannot be empty";
    public static final String NOT_EMPTY_PASSWORD_CONFIRMATION = "Password confirmation cannot be empty";
    public static final String NOT_EMPTY_USERNAME = "Username cannot be empty";
    public static final String PASSWORDS_NOT_MATCH = "Passwords do not match";
    public static final String TOO_LARGE_EMAIL =
        "E-mail too large! Max allowed length is " +
            AuthenticationValidations.MAX_EMAIL_LENGTH +
            " characters";
    public static final String TOO_LARGE_EMAIL_CONFIRMATION =
        "E-mail confirmation too large! Max allowed length is " +
            AuthenticationValidations.MAX_EMAIL_LENGTH +
            " characters";

    private AuthenticationValidationMessages() {}
}
