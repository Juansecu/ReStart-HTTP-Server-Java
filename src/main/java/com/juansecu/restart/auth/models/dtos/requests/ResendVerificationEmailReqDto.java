package com.juansecu.restart.auth.models.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResendVerificationEmailReqDto {
    @Min(
        message = "The user ID must be greater than 0",
        value = 1
    )
    @NotNull(
        message = "The user ID is required"
    )
    private Integer userId;
}
