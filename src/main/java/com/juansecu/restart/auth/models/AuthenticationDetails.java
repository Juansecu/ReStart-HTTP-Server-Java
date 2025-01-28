package com.juansecu.restart.auth.models;

import com.juansecu.restart.users.models.entities.UserEntity;

public record AuthenticationDetails (
    boolean isUserAuthenticated,
    String token,
    UserEntity user
) {}
