package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.RecipeDTO;
import com.zephsie.wellbeing.models.entity.Recipe;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.services.api.IRecipeService;
import com.zephsie.wellbeing.utils.converters.ErrorsToMapConverter;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.views.EntityView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final IRecipeService recipeService;

    private final ErrorsToMapConverter errorsToMapConverter;

    @Autowired
    public RecipeController(IRecipeService recipeService,
                            ErrorsToMapConverter errorsToMapConverter) {

        this.recipeService = recipeService;
        this.errorsToMapConverter = errorsToMapConverter;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Recipe> read(@PathVariable("id") UUID id) {

        return recipeService.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Recipe with id " + id + " not found"));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Recipe> create(@RequestBody @Valid RecipeDTO recipeDTO,
                                         BindingResult bindingResult,
                                         @AuthenticationPrincipal UserDetailsImp userDetails) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.create(recipeDTO, userDetails.getUser()));
    }

    @GetMapping(produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Page<Recipe>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(recipeService.read(page, size));
    }
}