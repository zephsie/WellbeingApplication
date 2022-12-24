package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.RecipeDTO;
import com.zephsie.wellbeing.models.entity.Recipe;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IRecipeService {
    Recipe create(RecipeDTO recipeDTO, User user);

    Optional<Recipe> read(UUID id);

    Page<Recipe> read(int page, int size);

    Recipe update(UUID id, RecipeDTO recipeDTO, LocalDateTime version, User user);
}
