package com.juansecu.restart.auth.repositories;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.juansecu.restart.auth.models.entities.RoleEntity;

@org.springframework.stereotype.Repository
public interface IRolesRepository extends Repository<RoleEntity, Integer> {
    Optional<RoleEntity> findByNameIgnoreCase(String name);
}
