package com.juansecu.restart.auth.models.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.juansecu.restart.auth.AuthenticationValidationMessages;
import com.juansecu.restart.auth.AuthenticationValidations;
import com.juansecu.restart.shared.constraints.FieldsValueMatchConstraint;

@Data
@FieldsValueMatchConstraint(
    message = AuthenticationValidationMessages.PASSWORDS_NOT_MATCH,
    field = "password",
    fieldMatch = "passwordConfirmation"
)
public class NewPasswordReqDto {
    @NotEmpty(message = AuthenticationValidationMessages.NOT_EMPTY_PASSWORD)
    @Size(
        max = AuthenticationValidations.MAX_PASSWORD_LENGTH,
        message = AuthenticationValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AuthenticationValidations.MIN_PASSWORD_LENGTH
    )
    private String password;
    @NotEmpty(message = AuthenticationValidationMessages.NOT_EMPTY_PASSWORD_CONFIRMATION)
    @Size(
        max = AuthenticationValidations.MAX_PASSWORD_LENGTH,
        message = AuthenticationValidationMessages.INVALID_PASSWORD_CONFIRMATION_LENGTH,
        min = AuthenticationValidations.MIN_PASSWORD_LENGTH
    )
    private String passwordConfirmation;
}
