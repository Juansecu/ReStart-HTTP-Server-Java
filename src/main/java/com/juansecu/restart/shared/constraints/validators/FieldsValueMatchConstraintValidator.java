package com.juansecu.restart.shared.constraints.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import com.juansecu.restart.shared.constraints.FieldsValueMatchConstraint;

public class FieldsValueMatchConstraintValidator implements ConstraintValidator<FieldsValueMatchConstraint, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(final FieldsValueMatchConstraint constraint) {
        this.field = constraint.field();
        this.fieldMatch = constraint.fieldMatch();
    }

    @Override
    public boolean isValid(
        final Object value,
        final ConstraintValidatorContext context
    ) {
        final Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        final Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);

        if (
            fieldValue == null ||
            fieldMatchValue == null
        )
            return false;

        return fieldValue.equals(fieldMatchValue);
    }
}
