package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.RecipeDTO;
import com.zephsie.wellbeing.models.entity.Recipe;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IRecipeService {
    Recipe create(RecipeDTO recipeDTO, User user);

    Optional<Recipe> read(UUID id, User user);

    Page<Recipe> read(int page, int size, User user);
}
