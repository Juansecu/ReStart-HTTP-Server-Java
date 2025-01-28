package com.juansecu.restart.verificationtokens;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.juansecu.restart.auth.AuthenticationApiService;
import com.juansecu.restart.auth.AuthenticationConstants;
import com.juansecu.restart.auth.enums.ERole;
import com.juansecu.restart.shared.utils.TimeUtil;
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


        final VerificationTokenEntity verificationToken = (VerificationTokenEntity) request.getAttribute(
            VerificationTokensConstants.VERIFICATION_TOKEN_REQUEST_ATTRIBUTE
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
                VerificationTokensConstants.HAS_TOKEN_EXPIRED_VIEW_ATTRIBUTE,
                true
            );
            model.addAttribute(
                VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                false
            );

            return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
        }

        VerificationTokensViewService.CONSOLE_LOGGER.info(
            "Verification token validated successfully. Updating uses count..."
        );

        verificationToken.setUsesCount(verificationToken.getUsesCount() + 1);

        this.verificationTokensRepository.save(verificationToken);

        switch (verificationTokenType) {
            case EMAIL_VERIFICATION_TOKEN -> {
                return this.verifyEmailVerificationToken(
                    verificationToken,
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
        final VerificationTokenEntity verificationToken,
        final Model model
    ) {
        if (
            verificationToken.getUsesCount() >= VerificationTokensConstants.EMAIL_VERIFICATION_TOKEN_MAXIMUM_USES ||
            verificationToken.getUser().getAuthorities().contains(
                new SimpleGrantedAuthority(ERole.USER.getRole())
            )
        ) {
            model.addAttribute(
                VerificationTokensConstants.IS_ALREADY_USED_TOKEN_VIEW_ATTRIBUTE,
                true
            );
            model.addAttribute(
                VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                false
            );
        } else {
            this.authenticationApiService.setUserAsVerified(
                verificationToken.getUser()
            );

            model.addAttribute(
                VerificationTokensConstants.IS_ALREADY_USED_TOKEN_VIEW_ATTRIBUTE,
                false
            );
            model.addAttribute(
                VerificationTokensConstants.IS_SUCCESSFUL_VIEW_ATTRIBUTE,
                true
            );
        }

        return AuthenticationConstants.EMAIL_VERIFICATION_TEMPLATE;
    }
}
