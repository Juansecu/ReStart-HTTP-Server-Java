package com.juansecu.restart.users.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.juansecu.restart.users.models.entities.UserEntity;

@Repository
public interface IUsersRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmailIgnoreCase(final String email);

    Optional<UserEntity> findByUsernameIgnoreCase(final String username);
}
