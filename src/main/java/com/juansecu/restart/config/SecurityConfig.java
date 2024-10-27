package com.juansecu.restart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${server.headers.content-security-policy}")
    private String contentSecurityPolicy;
    @Value("${server.headers.permissions-policy}")
    private String permissionsPolicy;

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(
        final HttpSecurity httpSecurity
    ) throws Exception {
        httpSecurity
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/**").permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp ->
                        csp.policyDirectives(this.contentSecurityPolicy)
                    )
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                    .httpStrictTransportSecurity(hsts ->
                        hsts
                            .includeSubDomains(true)
                            .maxAgeInSeconds(31536000)
                            .preload(true)
                    )
                    .referrerPolicy(referrerPolicyConfig ->
                        referrerPolicyConfig.policy(
                            ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER
                        )
                    )
                    .xssProtection(xssConfig ->
                        xssConfig.headerValue(
                            XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK
                        )
                    )
                    .permissionsPolicy(permissions ->
                        permissions.policy(this.permissionsPolicy)
                    )
            );

        return httpSecurity.build();
    }
}
