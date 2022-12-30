package com.zephsie.wellbeing.utils.validations.annotations;

import com.zephsie.wellbeing.utils.validations.validators.RecipeOrProductValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = RecipeOrProductValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RecipeOrProduct {
    String message() default "Either a recipe or a product is required, but not both";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}