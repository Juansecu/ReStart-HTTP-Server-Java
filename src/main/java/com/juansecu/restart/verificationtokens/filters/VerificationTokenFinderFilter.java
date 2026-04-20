package com.juansecu.restart.verificationtokens.filters;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.restart.auth.models.AuthenticationDetails;
import com.juansecu.restart.auth.utils.JwtAuthenticationValidationUtil;
import com.juansecu.restart.users.UsersApiService;
import com.juansecu.restart.users.UsersConstants;
import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.VerificationTokensApiService;
import com.juansecu.restart.verificationtokens.VerificationTokensConstants;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.restart.verificationtokens.models.entities.VerificationTokenEntity;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Component
@RequiredArgsConstructor
public class VerificationTokenFinderFilter extends OncePerRequestFilter {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(VerificationTokenFinderFilter.class);
    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();
    private static final PathPattern VERIFICATION_PATTERN =
        PATH_PATTERN_PARSER.parse("/verification-tokens/verify");

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
        int userId;
        Optional<VerificationTokenEntity> verificationToken;
        EVerificationTokenType verificationTokenType;

        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );
        final String encryptedToken = request.getParameter("token");
        final boolean isUserAuthenticated = authenticationDetails.isUserAuthenticated();
        final String tokenType = request.getParameter("type");
        final String userIdParameter = request.getParameter("userId");

        if (encryptedToken == null || tokenType == null || userIdParameter == null) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "Missing required request parameters"
            );

            return;
        } else if (!userIdParameter.matches("\\d+")) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The user ID is not a valid integer"
            );

            return;
        }

        userId = Integer.parseInt(userIdParameter);

        VerificationTokenFinderFilter.CONSOLE_LOGGER.info(
            "Verifying if user is authenticated and if the user ID matches with the token..."
        );

        if (isUserAuthenticated) {
            if (userId != authenticationDetails.user().getUserId()) {
                VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                    "The user ID does not match with the authenticated user account"
                );

                return;
            }

            user = Optional.of(authenticationDetails.user());
        } else user = this.usersApiService.findById(userId);

        if (user.isEmpty()) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The user does not exist"
            );

            return;
        }

        try {
            verificationTokenType = EVerificationTokenType.valueOf(tokenType);
        } catch (final IllegalArgumentException illegalArgumentException) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The verification token type is not valid"
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
        final PathContainer pathContainer = PathContainer.parsePath(
            request.getRequestURI()
        );

        return !VERIFICATION_PATTERN.matches(pathContainer);
    }
}
