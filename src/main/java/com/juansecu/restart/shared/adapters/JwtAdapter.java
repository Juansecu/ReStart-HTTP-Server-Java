package com.juansecu.restart.shared.adapters;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.juansecu.restart.auth.AuthenticationConstants;

@Component
public class JwtAdapter {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(JwtAdapter.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateJsonWebToken(
        final String subject,
        final String issuer
    ) {
        final Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret);

        JwtAdapter.CONSOLE_LOGGER.info("Generating JSON Web Token...");

        return JWT.create()
            .withSubject(subject)
            .withIssuer(issuer)
            .withExpiresAt(
                new Date(
                    new Date().getTime() + AuthenticationConstants.MINUTES_OF_VALID_AUTHENTICATION_TOKEN * 60 * 1000
                )
            )
            .sign(algorithm);
    }

    public String getSubject(final String token) {
        return JWT.decode(token).getSubject();
    }

    public boolean isValidJsonWebToken(final String token) {
        DecodedJWT decodedJwt;

        final Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret);
        final Date today = new Date();

        JwtAdapter.CONSOLE_LOGGER.info("Validating JSON Web Token...");

        try {
            decodedJwt = JWT.decode(token);

            if (decodedJwt.getExpiresAt().before(today)) {
                JwtAdapter.CONSOLE_LOGGER.error("JSON Web Token is already expired");
                return false;
            }

            JWT.require(algorithm).build().verify(decodedJwt);

            JwtAdapter.CONSOLE_LOGGER.info("JSON Web Token validated successfully");

            return true;
        } catch (JWTVerificationException jwtVerificationException) {
            JwtAdapter.CONSOLE_LOGGER.error(
                "Error while trying to validate JSON Web Token:\n{}",
                jwtVerificationException.getMessage()
            );

            return false;
        }
    }
}
