package com.juansecu.restart.auth.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.juansecu.restart.users.models.entities.UserEntity;

@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private final transient UserEntity user;

    public UserRegisterEvent(final UserEntity user) {
        super(user);
        this.user = user;
    }
}
