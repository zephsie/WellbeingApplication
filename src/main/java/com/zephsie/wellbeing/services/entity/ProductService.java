package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.ProductDTO;
import com.zephsie.wellbeing.models.entity.Product;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.repositories.ProductRepository;
import com.zephsie.wellbeing.services.api.IProductService;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.exceptions.WrongVersionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    private final IEntityDTOConverter<Product, ProductDTO> productConverter;

    @Autowired
    public ProductService(ProductRepository productRepository, IEntityDTOConverter<Product, ProductDTO> productConverter) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> read(UUID id, User user) {
        return productRepository.findByIdAndUser(id, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> read(int page, int size, User user) {
        return productRepository.findAllByUser(Pageable.ofSize(size).withPage(page), user);
    }

    @Override
    @Transactional
    public Product create(ProductDTO productDTO, User user) {
        Product product = productConverter.convertToEntity(productDTO);
        product.setUser(user);

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product update(UUID id, ProductDTO productDTO, LocalDateTime version, User user) {
        Optional<Product> optionalProduct = productRepository.findByIdAndUser(id, user);

        Product existingProduct = optionalProduct.orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));

        if (!existingProduct.getDtUpdate().equals(version)) {
            throw new WrongVersionException("Product with id " + id + " has been updated");
        }

        existingProduct.setTitle(productDTO.getTitle());
        existingProduct.setWeight(productDTO.getWeight());
        existingProduct.setCalories(productDTO.getCalories());
        existingProduct.setFats(productDTO.getFats());
        existingProduct.setCarbohydrates(productDTO.getCarbohydrates());
        existingProduct.setProteins(productDTO.getProteins());

        return productRepository.save(existingProduct);
    }
}