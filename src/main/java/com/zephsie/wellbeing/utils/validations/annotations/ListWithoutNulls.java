package com.zephsie.wellbeing.utils.validations.annotations;

import com.zephsie.wellbeing.utils.validations.validators.ListWithoutNullsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ListWithoutNullsValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ListWithoutNulls {
    String message() default "List cannot contain empty elements";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}