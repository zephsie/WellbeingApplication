package com.zephsie.wellbeing.repositories;

import com.zephsie.wellbeing.models.entity.Recipe;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    @NonNull
    @EntityGraph(value = "recipeWithCompositionsAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    Page<Recipe> findAllByUser(@NonNull Pageable pageable, @NonNull User user);

    @NonNull
    @EntityGraph(value = "recipeWithCompositionsAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Recipe> findByIdAndUser(@NonNull UUID id, @NonNull User user);
}
