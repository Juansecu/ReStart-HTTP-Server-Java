package com.juansecu.restart.auth.models.dtos.requests;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ResendVerificationEmailReqDto {
    @Min(
        message = "The user ID must be greater than 0",
        value = 1
    )
    private Integer userId;
}
