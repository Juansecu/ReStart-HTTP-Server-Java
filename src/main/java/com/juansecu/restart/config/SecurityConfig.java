package com.juansecu.restart.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import com.juansecu.restart.auth.enums.ERole;
import com.juansecu.restart.auth.filters.JwtAuthenticationFilter;
import com.juansecu.restart.verificationtokens.filters.VerificationTokenFinderFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${server.headers.content-security-policy}")
    private String contentSecurityPolicy;
    @Value("${server.headers.permissions-policy}")
    private String permissionsPolicy;

    private final String[] nonAuthenticationRequiredEndpoints;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final VerificationTokenFinderFilter verificationTokenFinderFilter;

    @Bean
    protected GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the "ROLE_" prefix from the authorities
        return new GrantedAuthorityDefaults("");
    }

    // Set the role hierarchy
    @Bean
    protected AuthorizationManagerFactory<Object> authorizationManagerFactory(
        final RoleHierarchy roleHierarchy
    ) {
        final DefaultAuthorizationManagerFactory<Object> factory = new DefaultAuthorizationManagerFactory<>();
        factory.setRoleHierarchy(roleHierarchy);
        return factory;
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl
            .withRolePrefix("")
            .role(ERole.ADMIN.getRole()).implies(ERole.MODERATOR.getRole())
            .role(ERole.MODERATOR.getRole()).implies(ERole.USER.getRole())
            .build();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(
        final HttpSecurity httpSecurity
    ) {
        httpSecurity
            .addFilterBefore(
                this.jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAfter(
                this.verificationTokenFinderFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers(
                        this.nonAuthenticationRequiredEndpoints
                    ).permitAll()
                    .anyRequest().authenticated()
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
                    .permissionsPolicyHeader(permissions ->
                        permissions.policy(this.permissionsPolicy)
                    )
            );

        return httpSecurity.build();
    }
}
