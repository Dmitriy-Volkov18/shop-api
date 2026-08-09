package com.example.shopapi.productVariant.services;

import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.repositories.ProductVariantRepository;
import com.example.shopapi.productVariant.dto.CreateProductVariantRequest;
import com.example.shopapi.productVariant.dto.UpdateProductVariantRequest;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.entities.VariantAttribute;
import com.example.shopapi.productVariant.enums.ProductVariantStatus;
import com.example.shopapi.common.exception.DuplicateSkuException;
import com.example.shopapi.common.exception.ProductVariantNotFoundException;
import com.example.shopapi.productVariant.interfaces.VariantAttributeRequest;
import com.example.shopapi.productVariant.mappers.ProductVariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantValidator validator;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantMapper variantMapper;

    @Transactional(readOnly = true)
    public List<ProductVariant> getVariants(
            Long productId
    ) {
        return variantRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public ProductVariant getVariant(
            Long productId,
            Long variantId
    ) {
        return variantRepository.findByIdAndProductId(
                        variantId,
                        productId
                )
                .orElseThrow(() ->
                        new ProductVariantNotFoundException(
                                "Variant not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public ProductVariant getById(
            Long variantId
    ) {
        return variantRepository.findById(variantId)
                .orElseThrow(() ->
                        new ProductVariantNotFoundException(
                                "Variant not found"
                        )
                );
    }

    @Transactional
    public ProductVariant createVariant(
            Product product,
            CreateProductVariantRequest request
    ) {
        validator.validateForCreate(product, request);

        ProductVariant variant = variantMapper.toEntity(request);

        attachAttributes(variant, request.attributes());

        variant.recalcStockStatus();
        product.addVariant(variant);

        return saveVariant(variant, request.sku());
    }

    @Transactional
    public ProductVariant updateVariant(
            ProductVariant variant,
            UpdateProductVariantRequest request
    ) {
        validator.validateForUpdate(variant, request);
        variantMapper.updateEntity(request, variant);

        variant.setDimensions(
                variantMapper.toDimensions(
                        request.dimensions()
                )
        );

        variant.clearAttributes();

        attachAttributes(variant, request.attributes());

        variant.recalcStockStatus();

        return saveVariant(variant, request.sku());
    }

    @Transactional
    public void deleteVariant(
            Product product,
            ProductVariant variant
    ) {
        product.removeVariant(variant);
    }

    private ProductVariant saveVariant(
            ProductVariant variant,
            String sku
    ) {
        try {
            return variantRepository.save(variant);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateSkuException(sku);
        }
    }

    public ProductVariant getBySku(String sku) {
        return variantRepository.findBySku(sku)
                .orElseThrow(() ->
                        new ProductVariantNotFoundException(sku)
                );
    }

    private void attachAttributes(
            ProductVariant variant,
            List<? extends VariantAttributeRequest> requests
    ) {
        for (VariantAttributeRequest request : requests) {
            VariantAttribute attribute = new VariantAttribute();

            attribute.setName(
                    request.name()
                            .trim()
                            .toLowerCase()
            );

            attribute.setValue(request.value());
            variant.addAttribute(attribute);
        }
    }

    private ProductVariant lockVariant(
            Long variantId
    ) {
        return variantRepository
                .findByIdForUpdate(
                        variantId
                )
                .orElseThrow(() ->
                        new ProductVariantNotFoundException(
                                variantId
                        )
                );
    }

    @Transactional
    public void increaseStock(
            Long variantId,
            int quantity
    ) {
        ProductVariant variant = lockVariant(variantId);
        variant.increaseStock(quantity);
    }

    @Transactional
    public void decreaseStock(
            Long variantId,
            int quantity
    ) {
        ProductVariant variant = lockVariant(variantId);
        variant.decreaseStock(quantity);
    }

    @Transactional
    public void deactivateAll(
            Product product
    ) {
        for (ProductVariant variant : product.getVariants()) {
            variant.deactivate();
        }
    }

    @Transactional(readOnly = true)
    public boolean hasActiveVariants(
            Product product
    ) {
        return product.getVariants()
                .stream()
                .anyMatch(
                        variant ->
                                variant.getStatus()
                                        == ProductVariantStatus.ACTIVE
                );
    }

    @Transactional(readOnly = true)
    public int getTotalStock(
            Product product
    ) {
        return product.getVariants()
                .stream()
                .mapToInt(
                        ProductVariant::getStockQuantity
                )
                .sum();
    }
}