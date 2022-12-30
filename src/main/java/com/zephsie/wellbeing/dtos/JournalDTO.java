package com.zephsie.wellbeing.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.wellbeing.utils.groups.BasicJournalFieldsGroup;
import com.zephsie.wellbeing.utils.groups.TotalJournalGroup;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.wellbeing.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.wellbeing.utils.validations.annotations.RecipeOrProduct;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RecipeOrProduct(message = "You must provide either a recipe or a product with valid Id", groups = {TotalJournalGroup.FirstOrder.class})
public class JournalDTO {
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @NotNull(message = "DtSupply is required", groups = {BasicJournalFieldsGroup.FirstOrder.class})
    private LocalDateTime dtSupply;

    @NotNull(message = "Weight is required", groups = {BasicJournalFieldsGroup.FirstOrder.class})
    private Integer weight;

    private MinProductDTO product;

    private MinRecipeDTO recipe;
}