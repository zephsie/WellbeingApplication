package com.zephsie.wellbeing.utils.validations.validators;

import com.zephsie.wellbeing.dtos.CompositionDTO;
import com.zephsie.wellbeing.dtos.MinProductDTO;
import com.zephsie.wellbeing.utils.validations.annotations.UniqueProductIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class UniqueProductIdsValidator implements ConstraintValidator<UniqueProductIds, Collection<CompositionDTO>> {

    @Override
    public boolean isValid(Collection<CompositionDTO> collection, ConstraintValidatorContext context) {
        return collection.stream()
                .map(CompositionDTO::getProduct)
                .map(MinProductDTO::getId)
                .distinct()
                .count() == collection.size();
    }
}