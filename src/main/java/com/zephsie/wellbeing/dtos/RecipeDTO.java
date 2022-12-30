package com.zephsie.wellbeing.dtos;

import com.zephsie.wellbeing.utils.groups.CompositionListGroup;
import com.zephsie.wellbeing.utils.groups.RecipeDTOGroup;
import com.zephsie.wellbeing.utils.validations.annotations.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {

    @NotBlank(message = "Title is required", groups = RecipeDTOGroup.FirstOrder.class)
    private String title;

    @NotEmptyList(message = "Composition is required", groups = {CompositionListGroup.FirstOrder.class})
    @ListWithoutNulls(message = "Composition must not contain empty elements", groups = {CompositionListGroup.SecondOrder.class})
    @ValidCompositionProperties(message = "Composition contains invalid properties", groups = {CompositionListGroup.ThirdOrder.class})
    @NotNullProductIds(message = "Product Ids cannot be empty", groups = {CompositionListGroup.FourthOrder.class})
    @UniqueProductIds(message = "Composition contains duplicate product Ids", groups = {CompositionListGroup.SixthOrder.class})
    private List<CompositionDTO> composition;
}
