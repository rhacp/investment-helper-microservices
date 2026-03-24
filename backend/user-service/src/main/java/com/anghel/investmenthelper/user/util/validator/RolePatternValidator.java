package com.anghel.investmenthelper.user.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RolePatternValidator implements ConstraintValidator<RolePattern, String> {

    private String[] subset;

    @Override
    public void initialize(RolePattern constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return Arrays.stream(subset).anyMatch(element -> element.equalsIgnoreCase(value));
    }
}
