package com.juansecu.restart.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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

    @Autowired
    @Qualifier("nonAuthenticationRequiredEndpoints")
    private final AntPathRequestMatcher[] nonAuthenticationRequiredEndpoints;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final VerificationTokenFinderFilter verificationTokenFinderFilter;

    @Bean
    protected GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the "ROLE_" prefix from the authorities
        return new GrantedAuthorityDefaults("");
    }

    // Set the role hierarchy
    @Bean
    protected MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        final RoleHierarchy roleHierarchy
    ) {
        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
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
    ) throws Exception {
        httpSecurity
            .addFilterBefore(
                this.jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAfter(
                this.verificationTokenFinderFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
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
                    .permissionsPolicy(permissions ->
                        permissions.policy(this.permissionsPolicy)
                    )
            );

        return httpSecurity.build();
    }
}
