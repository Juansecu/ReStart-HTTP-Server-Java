package com.juansecu.restart.shared.providers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class IssuerDetailsProvider {
    @Value("${mail.from-account-related-email-address}")
    private String fromAccountRelatedEmailAddress;
    @Value("${issuer.account-related.name}")
    private String issuerAccountRelatedName;
    @Value("${issuer.name}")
    private String issuerName;
    @Value("${issuer.support-address}")
    private String issuerSupportAddress;
}
