package com.zephsie.wellbeing.utils.validations.validators;

import com.zephsie.wellbeing.utils.validations.annotations.NotEmptyList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        return collection != null && !collection.isEmpty();
    }
}