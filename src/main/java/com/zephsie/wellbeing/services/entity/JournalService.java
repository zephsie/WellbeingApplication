package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.JournalDTO;
import com.zephsie.wellbeing.models.entity.*;
import com.zephsie.wellbeing.repositories.JournalRepository;
import com.zephsie.wellbeing.repositories.ProductRepository;
import com.zephsie.wellbeing.repositories.RecipeRepository;
import com.zephsie.wellbeing.services.api.IJournalService;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JournalService implements IJournalService {
    private final JournalRepository journalRepository;

    private final ProductRepository productRepository;

    private final RecipeRepository recipeRepository;

    @Autowired
    public JournalService(JournalRepository journalRepository, ProductRepository productRepository, RecipeRepository recipeRepository) {
        this.journalRepository = journalRepository;
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Journal createWithProduct(JournalDTO journalDTO, User user) {
        Optional<Product> optionalProduct = productRepository.findById(journalDTO.getProduct().getId());

        Product product = optionalProduct.orElseThrow(() ->
                new NotFoundException("Product with id " + journalDTO.getProduct().getId() + " not found"));

        JournalProduct journalProduct = new JournalProduct();

        journalProduct.setDtSupply(journalDTO.getDtSupply());
        journalProduct.setProduct(product);
        journalProduct.setUser(user);
        journalProduct.setWeight(journalDTO.getWeight());

        return journalRepository.save(journalProduct);
    }

    @Override
    public Journal createWithRecipe(JournalDTO journalDTO, User user) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(journalDTO.getRecipe().getId());

        Recipe recipe = optionalRecipe.orElseThrow(() ->
                new NotFoundException("Recipe with id " + journalDTO.getRecipe().getId() + " not found"));

        JournalRecipe journalRecipe = new JournalRecipe();

        journalRecipe.setDtSupply(journalDTO.getDtSupply());
        journalRecipe.setRecipe(recipe);
        journalRecipe.setUser(user);
        journalRecipe.setWeight(journalDTO.getWeight());

        return journalRepository.save(journalRecipe);
    }

    @Override
    public Optional<Journal> read(UUID id, User user) {
        return journalRepository.findByIdAndUser(id, user);
    }

    @Override
    public Page<Journal> read(int page, int size, User user) {
        return journalRepository.findAllByUser(Pageable.ofSize(size).withPage(page), user);
    }
}