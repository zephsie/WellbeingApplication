package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.ProductDTO;
import com.zephsie.wellbeing.models.entity.Product;
import com.zephsie.wellbeing.models.entity.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {
    Product create(ProductDTO productDTO, User user);

    Optional<Product> read(UUID id);

    Page<Product> read(int page, int size);

    Product update(UUID id, ProductDTO productDTO, LocalDateTime version, User user);
}
