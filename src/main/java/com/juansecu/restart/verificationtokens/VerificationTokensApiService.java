package com.juansecu.restart.verificationtokens;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import com.juansecu.restart.shared.utils.TimeUtil;
import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.restart.verificationtokens.models.entities.VerificationTokenEntity;
import com.juansecu.restart.verificationtokens.repositories.IVerificationTokensRepository;

@RequiredArgsConstructor
@Service
public class VerificationTokensApiService {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(VerificationTokensApiService.class);

    private final IVerificationTokensRepository verificationTokensRepository;
    private final TextEncryptor verificationTokensTextEncryptor;

    public void deleteVerificationToken(
        final VerificationTokenEntity verificationToken
    ) {
        VerificationTokensApiService.CONSOLE_LOGGER.info(
            "Deleting {} for user {}...",
            verificationToken.getType(),
            verificationToken.getUserId()
        );

        this.verificationTokensRepository.delete(verificationToken);
    }

    public VerificationTokenEntity generateVerificationToken(
        final UserEntity user,
        final EVerificationTokenType verificationTokenType,
        final Optional<String> metadata
        ) {
        VerificationTokensApiService.CONSOLE_LOGGER.info(
            "Generating {}...",
            verificationTokenType
        );

        LocalDateTime tokenExpirationDateTime;
        long tokenExpirationMilliseconds;
        VerificationTokenEntity verificationToken;

        final UUID token = UUID.randomUUID();

        tokenExpirationDateTime = switch (verificationTokenType) {
            case DELETE_ACCOUNT_TOKEN -> LocalDateTime
                .now()
                .plusMinutes(
                    VerificationTokensConstants.MINUTES_OF_EXPIRING_DELETE_ACCOUNT_TOKEN
                );
            case EMAIL_VERIFICATION_TOKEN -> LocalDateTime
                .now()
                .plusHours(
                    VerificationTokensConstants.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
                );
            case RESET_PASSWORD_TOKEN -> LocalDateTime
                .now()
                .plusMinutes(
                    VerificationTokensConstants.MINUTES_OF_EXPIRING_RESET_PASSWORD_TOKEN
                );
            case UPDATE_EMAIL_TOKEN -> LocalDateTime
                .now()
                .plusMinutes(
                    VerificationTokensConstants.MINUTES_OF_EXPIRING_UPDATE_EMAIL_TOKEN
                );
        };

        tokenExpirationMilliseconds = TimeUtil.localDateTimeToMilliseconds(
            tokenExpirationDateTime
        );

        VerificationTokensApiService.CONSOLE_LOGGER.info(
            "Saving {} for user {}...",
            verificationTokenType,
            user.getUsername()
        );

        verificationToken = metadata
            .map(metadataValue -> new VerificationTokenEntity(
                token,
                verificationTokenType,
                metadataValue,
                new Date(tokenExpirationMilliseconds),
                user.getUserId()
            ))
            .orElseGet(() -> new VerificationTokenEntity(
                token,
                verificationTokenType,
                user.getUserId(),
                new Date(tokenExpirationMilliseconds)
            ));

        return this.verificationTokensRepository.save(verificationToken);
    }

    public String getEncryptedToken(final String decryptedToken) {
        return this.verificationTokensTextEncryptor.encrypt(
            decryptedToken
        );
    }

    public Optional<VerificationTokenEntity> getVerificationToken(
        final EVerificationTokenType verificationTokenType,
        final UserEntity user
    ) {
        VerificationTokensApiService.CONSOLE_LOGGER.info(
            "Getting {} for user {}...",
            verificationTokenType,
            user.getUsername()
        );

        return this.verificationTokensRepository.findByTypeAndUser(
            verificationTokenType,
            user
        );
    }

    public Optional<VerificationTokenEntity> getVerificationToken(
        final String encryptedToken,
        final EVerificationTokenType verificationTokenType,
        final UserEntity user
    ) {
        UUID decryptedToken;

        VerificationTokensApiService.CONSOLE_LOGGER.info(
            "Getting {} for user {}...",
            verificationTokenType,
            user.getUsername()
        );

        try {
            decryptedToken = UUID.fromString(
                this.verificationTokensTextEncryptor.decrypt(
                    encryptedToken
                )
            );
        } catch (final IllegalArgumentException illegalArgumentException) {
            VerificationTokensApiService.CONSOLE_LOGGER.error(
                "Invalid token"
            );

            return Optional.empty();
        }

        return this.verificationTokensRepository.findByTokenAndTypeAndUser(
            decryptedToken,
            verificationTokenType,
            user
        );
    }
}
