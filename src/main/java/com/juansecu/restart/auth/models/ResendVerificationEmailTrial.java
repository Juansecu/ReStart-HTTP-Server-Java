package com.juansecu.restart.auth.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(
    // Time to Live: 1 day
    timeToLive = 86400,
    value = "ResendVerificationEmailTrials"
)
@RequiredArgsConstructor
@Setter
public class ResendVerificationEmailTrial implements Serializable {
    @Id
    @NonNull
    private Integer userId;
    @NonNull
    private Integer trialsAmount;
    @NonNull
    private Date createdAt;
    private Date updatedAt;
}
