package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.ProductDTO;
import com.zephsie.wellbeing.models.entity.Product;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.services.api.IProductService;
import com.zephsie.wellbeing.utils.converters.ErrorsToMapConverter;
import com.zephsie.wellbeing.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.wellbeing.utils.exceptions.NotFoundException;
import com.zephsie.wellbeing.utils.views.ProductView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final ErrorsToMapConverter errorsToMapConverter;

    @Autowired
    public ProductController(IProductService productService,
                             UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter,
                             ErrorsToMapConverter errorsToMapConverter) {

        this.productService = productService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.errorsToMapConverter = errorsToMapConverter;
    }

    @JsonView(ProductView.System.class)
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Product> read(@PathVariable("id") UUID id) {

        return productService.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
    }

    @JsonView(ProductView.System.class)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> create(@RequestBody @Valid ProductDTO productDTO,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal UserDetailsImp userDetails) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(productService.create(productDTO, userDetails.getUser()));
    }

    @JsonView(ProductView.System.class)
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Product>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(productService.read(page, size));
    }

    @JsonView(ProductView.System.class)
    @PutMapping(value = "/{id}/version/{version}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> update(@PathVariable("id") UUID id,
                                          @PathVariable("version") long version,
                                          @RequestBody @Valid ProductDTO productDTO,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal UserDetailsImp userDetails) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(errorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(productService.update(id, productDTO,
                unixTimeToLocalDateTimeConverter.convert(version), userDetails.getUser()));
    }

    @DeleteMapping(value = "/{id}/version/{version}", produces = "application/json")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id,
                                       @PathVariable("version") long version,
                                       @AuthenticationPrincipal UserDetailsImp userDetailsImp) {

        productService.delete(id, unixTimeToLocalDateTimeConverter.convert(version), userDetailsImp.getUser());

        return ResponseEntity.ok().build();
    }
}