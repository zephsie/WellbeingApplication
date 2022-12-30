package com.zephsie.wellbeing.utils.validations.validators;

import com.zephsie.wellbeing.utils.validations.annotations.ListWithoutNulls;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Objects;

public class ListWithoutNullsValidator implements ConstraintValidator<ListWithoutNulls, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        return collection.stream().noneMatch(Objects::isNull);
    }
}