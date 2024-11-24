package com.juansecu.restart.auth.models.dtos.responses;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.juansecu.restart.auth.enums.ERegisterError;
import com.juansecu.restart.shared.models.dtos.responses.BasicResDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RegisterResDto extends BasicResDto {
    private ERegisterError errorCode;

    public RegisterResDto(
        final boolean isSuccessful,
        final ERegisterError errorCode,
        final List<String> errors,
        final String message,
        final Object data
    ) {
        super(
            isSuccessful,
            errorCode,
            errors,
            message,
            data
        );
        this.errorCode = errorCode;
    }
}
