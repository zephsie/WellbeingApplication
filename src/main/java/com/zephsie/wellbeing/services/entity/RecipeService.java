package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.CompositionDTO;
import com.zephsie.wellbeing.dtos.MinProductDTO;
import com.zephsie.wellbeing.dtos.RecipeDTO;
import com.zephsie.wellbeing.models.entity.Composition;
import com.zephsie.wellbeing.models.entity.Product;
import com.zephsie.wellbeing.models.entity.Recipe;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.repositories.ProductRepository;
import com.zephsie.wellbeing.repositories.RecipeRepository;
import com.zephsie.wellbeing.services.api.IRecipeService;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class RecipeService implements IRecipeService {
    private final RecipeRepository recipeRepository;

    private final ProductRepository productRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, ProductRepository productRepository) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Recipe create(RecipeDTO recipeDTO, User user) {
        List<Product> products = productRepository.findAllById(recipeDTO.getComposition()
                .stream().map(CompositionDTO::getProduct)
                .map(MinProductDTO::getUuid)
                .toList());

        if (products.size() != recipeDTO.getComposition().size()) {
            throw new NotFoundException("Not all products are found");
        }

        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.setTitle(recipeDTO.getTitle());

        List<Composition> compositions = IntStream.range(0, products.size())
                .mapToObj(i -> {
                    Composition composition = new Composition();
                    composition.setProduct(products.get(i));
                    composition.setWeight(recipeDTO.getComposition().get(i).getWeight());
                    composition.setRecipe(recipe);
                    return composition;
                })
                .toList();

        recipe.setComposition(compositions);

        return recipeRepository.save(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recipe> read(UUID id) {
        return recipeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Recipe> read(int page, int size) {
        return recipeRepository.findAll(Pageable.ofSize(size).withPage(page));
    }
}