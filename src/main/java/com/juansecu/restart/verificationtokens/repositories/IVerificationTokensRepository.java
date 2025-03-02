package com.juansecu.restart.verificationtokens.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.restart.verificationtokens.models.entities.VerificationTokenEntity;

public interface IVerificationTokensRepository extends CrudRepository<VerificationTokenEntity, Integer> {
    Optional<VerificationTokenEntity> findByTokenAndTypeAndUser(
        UUID decryptedToken,
        EVerificationTokenType type,
        UserEntity user
    );

    Optional<VerificationTokenEntity> findByTypeAndUser(
        EVerificationTokenType type,
        UserEntity user
    );
}
