package com.juansecu.restart.shared.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.juansecu.restart.shared.constraints.validators.FieldsValueMatchConstraintValidator;

@Constraint(validatedBy = FieldsValueMatchConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FieldsValueMatchConstraint {
    String field();

    String fieldMatch();

    Class<?>[] groups() default {};

    String message() default "Fields values do not match";

    Class<? extends Payload>[] payload() default {};
}
