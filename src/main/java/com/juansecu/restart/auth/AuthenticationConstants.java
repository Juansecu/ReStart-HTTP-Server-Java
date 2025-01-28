package com.juansecu.restart.auth;

public class AuthenticationConstants {
    public static final String AUTHENTICATION_COOKIE_NAME = "token";
    public static final String EMAIL_VERIFICATION_TEMPLATE = "auth/email-verification";
    public static final String IS_USER_AUTHENTICATED_REQUEST_ATTRIBUTE = "isUserAuthenticated";
    public static final int MINUTES_OF_VALID_AUTHENTICATION_TOKEN = 30;

    private AuthenticationConstants() {}
}
