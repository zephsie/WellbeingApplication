package com.zephsie.wellbeing.utils.converters.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.wellbeing.dtos.ProductDTO;
import com.zephsie.wellbeing.models.entity.Product;
import com.zephsie.wellbeing.utils.converters.api.IEntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOConverter implements IEntityDTOConverter<Product, ProductDTO> {

    private final ObjectMapper objectMapper;

    @Autowired
    public ProductDTOConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Product convertToEntity(ProductDTO dto) {
        return objectMapper.convertValue(dto, Product.class);
    }
}
