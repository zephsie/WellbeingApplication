package com.zephsie.wellbeing.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompositionDTO {
    @NotNull(message = "Product is required")
    private MinProductDTO product;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be greater than 0")
    private Integer weight;
}