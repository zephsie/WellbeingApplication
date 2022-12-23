package com.zephsie.wellbeing.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be greater than 0")
    private Integer weight;

    @NotNull(message = "Calories is required")
    @Min(value = 0, message = "Calories cannot be less than 0")
    private Integer calories;

    @NotNull(message = "Proteins is required")
    @Min(value = 0, message = "Proteins cannot be less than 0")
    private Double proteins;

    @NotNull(message = "Fats is required")
    @Min(value = 0, message = "Fats cannot be less than 0")
    private Double fats;

    @NotNull(message = "Carbohydrates is required")
    @Min(value = 0, message = "Carbohydrates cannot be less than 0")
    private Double carbohydrates;
}
