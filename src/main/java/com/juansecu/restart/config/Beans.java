package com.juansecu.restart.config;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class Beans {
    @Value("${server.non-authentication-required-endpoints}")
    private String[] nonAuthenticationRequiredEndpoints;
    @Value("${verification-tokens.salt-key}")
    private String verificationTokensSaltKey;
    @Value("${verification-tokens.security-key}")
    private String verificationTokensSecurityKey;

    @Bean(name = "nonAuthenticationRequiredEndpoints")
    protected AntPathRequestMatcher[] nonAuthenticationRequiredEndpoints() {
        return Arrays
            .stream(this.nonAuthenticationRequiredEndpoints)
            .map(AntPathRequestMatcher::new)
            .toArray(AntPathRequestMatcher[]::new);
    }

    protected ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "verificationTokensTextEncryptor")
    protected TextEncryptor verificationTokensTextEncryptor() {
        return Encryptors.text(
            this.verificationTokensSecurityKey,
            this.verificationTokensSaltKey
        );
    }
}
