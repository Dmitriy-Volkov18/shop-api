package com.example.shopapi.productVariant.facades;

import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.productVariant.dto.CreateProductVariantRequest;
import com.example.shopapi.productVariant.dto.ProductVariantResponse;
import com.example.shopapi.productVariant.dto.UpdateProductVariantRequest;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.services.ProductVariantResponseService;
import com.example.shopapi.productVariant.services.ProductVariantService;
import com.example.shopapi.auth.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantFacade {

    private final ProductService productService;
    private final ProductVariantService variantService;
    private final AuthorizationService authorizationService;
    private final ProductVariantResponseService productVariantResponseService;

    public ProductVariantResponse createVariant(
            Long productId,
            CreateProductVariantRequest request
    ) {
        Product product = getProduct(productId);
        authorizationService.requireProductAccess(product);

        ProductVariant variant =
                variantService.createVariant(
                        product,
                        request
                );

        return productVariantResponseService.toResponse(variant);
    }

    public List<ProductVariantResponse> getVariants(
            Long productId
    ) {
        return variantService.getVariants(productId)
                .stream()
                .map(productVariantResponseService::toResponse)
                .toList();
    }

    public ProductVariantResponse updateVariant(
            Long productId,
            Long variantId,
            UpdateProductVariantRequest request
    ) {
        Product product = getProduct(productId);
        authorizationService.requireProductAccess(product);

        ProductVariant variant =
                variantService.getVariant(
                        productId,
                        variantId
                );

        ProductVariant updated =
                variantService.updateVariant(
                        variant,
                        request
                );

        return productVariantResponseService.toResponse(updated);
    }

    public void deleteVariant(
            Long productId,
            Long variantId
    ) {
        Product product = getProduct(productId);
        authorizationService.requireProductAccess(product);

        ProductVariant variant =
                variantService.getVariant(
                        productId,
                        variantId
                );

        variantService.deleteVariant(
                product,
                variant
        );
    }

    public ProductVariantResponse getBySku(
            String sku
    ) {
        ProductVariant variant = variantService.getBySku(sku);

        return productVariantResponseService.toResponse(variant);
    }

    private Product getProduct(Long id){
        return productService.getProduct(id);
    }

}