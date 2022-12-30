package com.zephsie.wellbeing.utils.validations.annotations;

import com.zephsie.wellbeing.utils.validations.validators.UniqueProductIdsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueProductIdsValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface UniqueProductIds {
    String message() default "Product Ids must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}