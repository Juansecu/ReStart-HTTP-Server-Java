package com.juansecu.restart.auth.events.listeners;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.restart.auth.events.UserRegisterEvent;
import com.juansecu.restart.shared.providers.HostDetailsProvider;
import com.juansecu.restart.shared.services.EmailService;
import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.VerificationTokensApiService;
import com.juansecu.restart.verificationtokens.VerificationTokensConstants;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.restart.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class UserRegisterListener implements ApplicationListener<UserRegisterEvent> {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(UserRegisterListener.class);

    @Value("${mail.new-account-email-verification-message.message}")
    private String newAccountEmailVerificationMessage;
    @Value("${mail.new-account-email-verification-message.subject}")
    private String newAccountEmailVerificationSubject;

    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final VerificationTokensApiService verificationTokensApiService;

    @Override
    public void onApplicationEvent(final UserRegisterEvent userRegisterEvent) {
        final UserEntity newUser = userRegisterEvent.getUser();
        final VerificationTokenEntity verificationToken = this.verificationTokensApiService.generateVerificationToken(
            newUser,
            EVerificationTokenType.EMAIL_VERIFICATION_TOKEN,
            Optional.empty()
        );
        final String verificationEmailMessage = this.replaceEmailVerificationParameters(
            this.verificationTokensApiService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            newUser
        );

        UserRegisterListener.CONSOLE_LOGGER.info(
            "Sending verification email to user {}...",
            newUser.getUsername()
        );

        if (
            !this.emailService.sendSimpleAccountManagementMessage(
                verificationEmailMessage,
                this.newAccountEmailVerificationSubject,
                newUser.getEmail()
            )
        ) {
            UserRegisterListener.CONSOLE_LOGGER.error(
                "Verification email could not be sent"
            );

            return;
        }

        UserRegisterListener.CONSOLE_LOGGER.info(
            "Verification email sent"
        );
    }

    private String replaceEmailVerificationParameters(
        final String encryptedToken,
        final UserEntity newUser
    ) {
        return this.newAccountEmailVerificationMessage
            .replace(
                "{hours_to_expire}",
                String.valueOf(
                    VerificationTokensConstants.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
                )
            )
            .replace("{username}", newUser.getUsername())
            .replace(
                "{user_account_verification_link}",
                this.hostDetailsProvider.getPublicAddress() +
                    "/verification-tokens/verify?token=" +
                    encryptedToken +
                    "&type=" +
                    EVerificationTokenType.EMAIL_VERIFICATION_TOKEN +
                    "&userId=" +
                    newUser.getUserId()
            );
    }
}
