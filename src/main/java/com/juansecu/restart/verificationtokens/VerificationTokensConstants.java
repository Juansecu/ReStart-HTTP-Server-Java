package com.juansecu.restart.verificationtokens;

public final class VerificationTokensConstants {
    public static final int DELETE_ACCOUNT_TOKEN_MAXIMUM_USES = 1;
    public static final int EMAIL_VERIFICATION_TOKEN_MAXIMUM_USES = 1;
    public static final String HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE = "hasTokenExpired";
    public static final int HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN = 24;
    public static final String IS_ALREADY_USED_TOKEN_VIEW_ATTRIBUTE = "isAlreadyUsedToken";
    public static final String IS_SUCCESSFUL_VIEW_ATTRIBUTE = "isSuccessful";
    public static final String IS_VALID_TOKEN_REQUEST_ATTRIBUTE = "isValidToken";
    public static final int MINUTES_OF_EXPIRING_DELETE_ACCOUNT_TOKEN = 10;
    public static final int MINUTES_OF_EXPIRING_RESET_PASSWORD_TOKEN = 10;
    public static final int MINUTES_OF_EXPIRING_UPDATE_EMAIL_TOKEN = 10;
    public static final int RESET_PASSWORD_TOKEN_MAXIMUM_USES = 2;
    public static final int UPDATE_EMAIL_TOKEN_MAXIMUM_USES = 1;
    public static final String VERIFICATION_TOKEN_REQUEST_ATTRIBUTE = "verificationToken";

    private VerificationTokensConstants() {}
}
