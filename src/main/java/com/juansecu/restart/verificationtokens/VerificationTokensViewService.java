package com.juansecu.restart.verificationtokens;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.juansecu.restart.auth.AuthenticationApiService;
import com.juansecu.restart.auth.AuthenticationConstants;
import com.juansecu.restart.shared.utils.TimeUtil;
import com.juansecu.restart.users.UsersConstants;
import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.restart.verificationtokens.models.entities.VerificationTokenEntity;
import com.juansecu.restart.verificationtokens.repositories.IVerificationTokensRepository;

@RequiredArgsConstructor
@Service
public class VerificationTokensViewService {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(VerificationTokensViewService.class);

    private final AuthenticationApiService authenticationApiService;
    private final IVerificationTokensRepository verificationTokensRepository;

    public String verify(
        final EVerificationTokenType verificationTokenType,
        final Model model,
        final HttpServletRequest request
    ) {
        if (
            !(
                (boolean) request.getAttribute(
                    VerificationTokensConstants.IS_VALID_TOKEN_REQUEST_ATTRIBUTE
                )
            )
        ) {
            VerificationTokensViewService.CONSOLE_LOGGER.error(
                "Invalid token"
            );

            model.addAttribute(
                VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                false
            );
            model.addAttribute(
                VerificationTokensConstants.HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE,
                false
            );

            return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
        }

        final UserEntity user = (UserEntity) request.getAttribute(
            UsersConstants.USER_REQUEST_ATTRIBUTE
        );
        final VerificationTokenEntity verificationToken = (VerificationTokenEntity) request.getAttribute(
            VerificationTokensConstants.VERIFICATION_TOKEN_REQUEST_ATTRIBUTE
        );

        if (
            verificationTokenType == EVerificationTokenType.EMAIL_VERIFICATION_TOKEN &&
            (
                verificationToken.getUsesCount() > VerificationTokensConstants.EMAIL_VERIFICATION_TOKEN_MAXIMUM_USES ||
                user.isVerified()
            )
        ) {
            model.addAttribute(
                VerificationTokensConstants.HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE,
                false
            );
            model.addAttribute(
                VerificationTokensConstants.IS_ALREADY_USED_TOKEN_VIEW_ATTRIBUTE,
                true
            );
            model.addAttribute(
                VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                false
            );

            return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
        }

        model.addAttribute(
            VerificationTokensConstants.IS_ALREADY_USED_TOKEN_VIEW_ATTRIBUTE,
            false
        );

        if (
            verificationToken.getExpiresAt().getTime() < TimeUtil.localDateTimeToMilliseconds(
                LocalDateTime.now()
            )
        ) {
            VerificationTokensViewService.CONSOLE_LOGGER.info(
                "Verification token expired"
            );

            model.addAttribute(
                UsersConstants.USER_ID_VIEW_ATTRIBUTE,
                user.getUserId()

            );
            model.addAttribute(
                VerificationTokensConstants.HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE,
                true
            );
            model.addAttribute(
                VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                false
            );

            return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
        }

        model.addAttribute(
            VerificationTokensConstants.HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE,
            false
        );

        VerificationTokensViewService.CONSOLE_LOGGER.info(
            "Verification token validated successfully. Updating uses count..."
        );

        verificationToken.setUsesCount(verificationToken.getUsesCount() + 1);

        this.verificationTokensRepository.save(verificationToken);

        switch (verificationTokenType) {
            case EMAIL_VERIFICATION_TOKEN -> {
                return this.verifyEmailVerificationToken(
                    user,
                    model
                );
            }
            // TODO: Remove default case when all verification token types are implemented
            default -> {
                VerificationTokensViewService.CONSOLE_LOGGER.error(
                    "Verification token type not supported: {}",
                    verificationTokenType
                );

                model.addAttribute(
                    VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                    false
                );
                model.addAttribute(
                    VerificationTokensConstants.HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE,
                    false
                );

                return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
            }
        }
    }

    private String verifyEmailVerificationToken(
        final UserEntity user,
        final Model model
    ) {
        this.authenticationApiService.setUserAsVerified(user);

        model.addAttribute(
            VerificationTokensConstants.IS_ALREADY_USED_TOKEN_VIEW_ATTRIBUTE,
            false
        );
        model.addAttribute(
            VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
            true
        );

        return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
    }
}
