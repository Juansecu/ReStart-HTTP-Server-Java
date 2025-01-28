package com.juansecu.restart.shared.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HostDetailsProvider {
    @Value("${server.public-address}")
    private String publicAddress;

    public String getPublicAddress() {
        return this.publicAddress.trim();
    }
}
