package com.example.shopapi.productVariant.controllers;

import com.example.shopapi.productVariant.facades.ProductVariantFacade;
import com.example.shopapi.productVariant.dto.CreateProductVariantRequest;
import com.example.shopapi.productVariant.dto.ProductVariantResponse;
import com.example.shopapi.productVariant.dto.UpdateProductVariantRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantFacade facade;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<ProductVariantResponse> getVariants(
            @PathVariable Long productId
    ) {
        return facade.getVariants(productId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductVariantResponse createVariant(
            @PathVariable Long productId,
            @Valid
            @RequestBody
            CreateProductVariantRequest request
    ) {
        return facade.createVariant(
                productId,
                request
        );
    }

    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductVariantResponse updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid
            @RequestBody
            UpdateProductVariantRequest request
    ) {
        return facade.updateVariant(
                productId,
                variantId,
                request
        );
    }

    @DeleteMapping("/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId
    ) {
        facade.deleteVariant(
                productId,
                variantId
        );
    }

    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ProductVariantResponse getBySku(
            @PathVariable String sku
    ) {
        return facade.getBySku(sku);
    }
}