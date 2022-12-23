package com.zephsie.wellbeing.repositories;

import com.zephsie.wellbeing.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);
}
