package com.zephsie.wellbeing.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Composition is required")
    private List<CompositionDTO> composition;
}
