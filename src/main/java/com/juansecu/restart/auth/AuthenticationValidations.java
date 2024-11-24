package com.juansecu.restart.auth;

public final class AuthenticationValidations {
    public static final String BIRTHDATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
    public static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final int MAX_EMAIL_LENGTH = 50;
    public static final int MAX_PASSWORD_LENGTH = 32;
    public static final int MAX_USERNAME_LENGTH = 12;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final String USERNAME_PATTERN = "^[0-9a-z_-]+$";

    private AuthenticationValidations() {}
}
