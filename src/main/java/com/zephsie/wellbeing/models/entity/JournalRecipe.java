package com.zephsie.wellbeing.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "journal_recipe", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
public class JournalRecipe extends Journal {

    @ManyToOne(targetEntity = Recipe.class)
    @JoinColumn(name = "recipe_id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.WithMappings.class)
    private Recipe recipe;
}