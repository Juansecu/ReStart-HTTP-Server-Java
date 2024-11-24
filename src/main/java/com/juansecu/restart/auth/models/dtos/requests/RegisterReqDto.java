package com.juansecu.restart.auth.models.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.juansecu.restart.auth.AuthenticationValidationMessages;
import com.juansecu.restart.auth.AuthenticationValidations;
import com.juansecu.restart.shared.constraints.FieldsValueMatchConstraint;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldsValueMatchConstraint(
    message = AuthenticationValidationMessages.EMAILS_NOT_MATCH,
    field = "email",
    fieldMatch = "emailConfirmation"
)
public class RegisterReqDto extends NewPasswordReqDto {
    @NotEmpty(
        message = AuthenticationValidationMessages.NOT_EMPTY_EMAIL
    )
    @Pattern(
        flags = {Pattern.Flag.CASE_INSENSITIVE},
        message = AuthenticationValidationMessages.INVALID_EMAIL,
        regexp = AuthenticationValidations.EMAIL_PATTERN
    )
    @Size(
        max = AuthenticationValidations.MAX_EMAIL_LENGTH,
        message = AuthenticationValidationMessages.TOO_LARGE_EMAIL
    )
    private String email;
    @NotEmpty(
        message = AuthenticationValidationMessages.NOT_EMPTY_EMAIL_CONFIRMATION
    )
    @Pattern(
        flags = {Pattern.Flag.CASE_INSENSITIVE},
        message = AuthenticationValidationMessages.INVALID_EMAIL_CONFIRMATION,
        regexp = AuthenticationValidations.EMAIL_PATTERN
    )
    @Size(
        max = AuthenticationValidations.MAX_EMAIL_LENGTH,
        message = AuthenticationValidationMessages.TOO_LARGE_EMAIL_CONFIRMATION
    )
    private String emailConfirmation;
    @NotEmpty(message = AuthenticationValidationMessages.NOT_EMPTY_USERNAME)
    @Pattern(
        flags = {Pattern.Flag.CASE_INSENSITIVE},
        message = AuthenticationValidationMessages.INVALID_USERNAME,
        regexp = AuthenticationValidations.USERNAME_PATTERN
    )
    @Size(
        max = AuthenticationValidations.MAX_USERNAME_LENGTH,
        message = AuthenticationValidationMessages.INVALID_USERNAME_LENGTH,
        min = AuthenticationValidations.MIN_USERNAME_LENGTH
    )
    private String username;
    @Pattern(
        message = AuthenticationValidationMessages.INVALID_BIRTHDATE,
        regexp = AuthenticationValidations.BIRTHDATE_PATTERN
    )
    @NotEmpty(message = AuthenticationValidationMessages.NOT_EMPTY_BIRTHDATE)
    private String birthdate;
}
