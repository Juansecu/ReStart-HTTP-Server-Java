package com.juansecu.restart.verificationtokens.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.restart.auth.AuthenticationConstants;
import com.juansecu.restart.auth.models.AuthenticationDetails;
import com.juansecu.restart.auth.utils.JwtAuthenticationValidationUtil;
import com.juansecu.restart.users.UsersApiService;
import com.juansecu.restart.users.UsersConstants;
import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.VerificationTokensApiService;
import com.juansecu.restart.verificationtokens.VerificationTokensConstants;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.restart.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class VerificationTokenFinderFilter extends OncePerRequestFilter {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(VerificationTokenFinderFilter.class);

    private final JwtAuthenticationValidationUtil jwtAuthenticationValidationUtil;
    private final UsersApiService usersApiService;
    private final VerificationTokensApiService verificationTokensApiService;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws IOException, ServletException {
        Optional<UserEntity> user;
        Optional<VerificationTokenEntity> verificationToken;
        EVerificationTokenType verificationTokenType;

        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );
        final String encryptedToken = request.getParameter("token");
        final boolean isUserAuthenticated = authenticationDetails.isUserAuthenticated();
        final int userId = Integer.parseInt(
            request.getParameter("userId")
        );

        request.setAttribute(
            AuthenticationConstants.IS_USER_AUTHENTICATED_REQUEST_ATTRIBUTE,
            isUserAuthenticated
        );

        if (isUserAuthenticated) {
            if (userId != authenticationDetails.user().getUserId()) {
                VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                    "The user ID does not match with the authenticated user account"
                );

                request.setAttribute(
                    VerificationTokensConstants.IS_VALID_TOKEN_REQUEST_ATTRIBUTE,
                    true
                );

                return;
            }

            user = Optional.of(authenticationDetails.user());
        } else
            user = this.usersApiService.findById(
                userId
            );

        if (user.isEmpty()) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The user does not exist"
            );

            request.setAttribute(
                VerificationTokensConstants.IS_VALID_TOKEN_REQUEST_ATTRIBUTE,
                false
            );

            return;
        }

        try {
            verificationTokenType = EVerificationTokenType.valueOf(
                request.getParameter("type")
            );
        } catch (final IllegalArgumentException illegalArgumentException) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The verification token type is not valid"
            );

            request.setAttribute(
                VerificationTokensConstants.IS_VALID_TOKEN_REQUEST_ATTRIBUTE,
                false
            );

            return;
        }

        verificationToken = this.verificationTokensApiService.getVerificationToken(
            encryptedToken,
            verificationTokenType,
            user.get()
        );

        if (verificationToken.isEmpty()) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The verification token does not exist"
            );

            request.setAttribute(
                VerificationTokensConstants.IS_VALID_TOKEN_REQUEST_ATTRIBUTE,
                false
            );

            return;
        }

        request.setAttribute(UsersConstants.USER_REQUEST_ATTRIBUTE, user.get());
        request.setAttribute(
            VerificationTokensConstants.IS_VALID_TOKEN_REQUEST_ATTRIBUTE,
            true
        );
        request.setAttribute(
            VerificationTokensConstants.VERIFICATION_TOKEN_REQUEST_ATTRIBUTE,
            verificationToken.get()
        );

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(
        @NonNull final HttpServletRequest request
    ) {
        final AntPathRequestMatcher[] excludedMatchers = {
            new AntPathRequestMatcher("/verification-tokens/verify")
        };

        return Arrays
            .stream(excludedMatchers)
            .noneMatch(
                (AntPathRequestMatcher matcher) -> matcher.matches(request)
            );
    }
}
