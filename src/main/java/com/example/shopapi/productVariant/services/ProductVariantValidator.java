package com.example.shopapi.productVariant.services;

import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.common.exception.DuplicateSkuException;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.dto.CreateProductVariantRequest;
import com.example.shopapi.productVariant.dto.UpdateProductVariantRequest;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.interfaces.VariantAttributeRequest;
import com.example.shopapi.productVariant.repositories.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.shopapi.common.constants.ProductConstants.MAX_ATTRIBUTES;
import static com.example.shopapi.common.constants.ProductConstants.MAX_VARIANTS;

@Service
@RequiredArgsConstructor
public class ProductVariantValidator {

    private final ProductVariantRepository variantRepository;

    public void validateForCreate(
            Product product,
            CreateProductVariantRequest request
    ) {
        validateVariantLimit(product);
        validateSku(request.sku());
        validateAttributes(request.attributes());
    }

    public void validateForUpdate(
            ProductVariant variant,
            UpdateProductVariantRequest request
    ) {
        if (!variant.getSku().equals(request.sku())) {
            validateSku(request.sku());
        }

        validateAttributes(request.attributes());
    }

    private void validateVariantLimit(Product product) {
        if (product.getVariants().size() >= MAX_VARIANTS) {
            throw new BadRequestException(
                    "Maximum " + MAX_VARIANTS + " variants allowed"
            );
        }
    }

    private void validateSku(String sku) {
        if (variantRepository.existsBySku(sku)) {
            throw new DuplicateSkuException(sku);
        }
    }

    private void validateAttributes(
            List<? extends VariantAttributeRequest> attributes
    ) {
        if (attributes == null || attributes.isEmpty()) {
            throw new BadRequestException(
                    "Variant must contain attributes"
            );
        }

        if (attributes.size() > MAX_ATTRIBUTES) {
            throw new BadRequestException(
                    "Maximum " + MAX_ATTRIBUTES + " attributes allowed"
            );
        }

        Set<String> unique = new HashSet<>();

        for (VariantAttributeRequest attribute : attributes) {
            String normalized =
                    attribute.name()
                            .trim()
                            .toLowerCase();

            if (!unique.add(normalized)) {
                throw new BadRequestException(
                        "Duplicate attribute: " + attribute.name()
                );
            }
        }
    }
}