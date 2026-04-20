package com.juansecu.restart.auth.filters;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.restart.auth.enums.EAuthenticationError;
import com.juansecu.restart.auth.models.AuthenticationDetails;
import com.juansecu.restart.auth.utils.JwtAuthenticationValidationUtil;
import com.juansecu.restart.shared.models.dtos.responses.BasicResDto;
import com.juansecu.restart.users.models.entities.UserEntity;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtAuthenticationValidationUtil jwtAuthenticationValidationUtil;
    private final String[] nonAuthenticationRequiredEndpoints;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authenticationToken;
        UserEntity user;

        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );

        if (!authenticationDetails.isUserAuthenticated()) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.error("Invalid authentication");

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            objectMapper.writeValue(
                response.getWriter(),
                new BasicResDto(
                    false,
                    EAuthenticationError.INVALID_AUTHENTICATION,
                    null,
                    "Invalid authentication",
                    null
                )
            );

            return;
        }

        user = authenticationDetails.user();

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.info(
                "User {} is already authenticated",
                user.getUsername()
            );

            filterChain.doFilter(request, response);

            return;
        }

        JwtAuthenticationFilter.CONSOLE_LOGGER.info(
            "Authenticating user {}...",
            user.getUsername()
        );

        authenticationToken = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(
        @NonNull final HttpServletRequest request
    ) {
        final String path = request.getRequestURI()
            .substring(request.getContextPath().length());

        return Arrays.asList(this.nonAuthenticationRequiredEndpoints).contains(path);
    }
}
