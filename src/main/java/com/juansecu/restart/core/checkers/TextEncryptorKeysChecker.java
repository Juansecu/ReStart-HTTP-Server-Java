package com.juansecu.restart.core.checkers;

import java.security.KeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TextEncryptorKeysChecker implements InitializingBean {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(
        TextEncryptorKeysChecker.class
    );

    @Value("${verification-tokens.salt-key}")
    private String verificationTokenSalt;
    @Value("${verification-tokens.security-key}")
    private String verificationTokenSecurityKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        TextEncryptorKeysChecker.CONSOLE_LOGGER.info(
            "Checking text encryptor keys..."
        );

        if (this.verificationTokenSalt.equals(this.verificationTokenSecurityKey)) {
            TextEncryptorKeysChecker.CONSOLE_LOGGER.error(
                "The verification token salt and security key must be different"
            );

            throw new KeyException(
                "The verification token salt and security key must be different"
            );
        }

        TextEncryptorKeysChecker.CONSOLE_LOGGER.info(
            "Text encryptor keys checked successfully"
        );
    }
}
