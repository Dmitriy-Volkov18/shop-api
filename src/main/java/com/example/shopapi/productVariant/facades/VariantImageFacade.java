package com.example.shopapi.productVariant.facades;

import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.productVariant.dto.VariantImageResponse;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.mappers.VariantImageMapper;
import com.example.shopapi.productVariant.services.ProductVariantService;
import com.example.shopapi.productVariant.services.VariantImageService;
import com.example.shopapi.product.services.ImageUploadService;
import com.example.shopapi.auth.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantImageFacade {

    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final AuthorizationService authorizationService;
    private final ImageUploadService imageUploadService;
    private final VariantImageService variantImageService;
    private final VariantImageMapper variantImageMapper;


    public List<VariantImageResponse> getImages(
            Long productId,
            Long variantId
    ) {
        ProductVariant variant =
                productVariantService.getVariant(
                        productId,
                        variantId
                );

        return variantImageService.getImages(variant);
    }


    public VariantImageResponse uploadImage(
            Long productId,
            Long variantId,
            MultipartFile file,
            boolean primaryImage
    ) {
        loadAuthorizedProduct(productId);

        ProductVariant variant =
                productVariantService.getVariant(
                        productId,
                        variantId
                );

        ImageMetadata metadata = imageUploadService.process(file);

        return variantImageService.addImage(
                variant,
                metadata,
                primaryImage
        );
    }

    public List<VariantImageResponse> uploadImages(
            Long productId,
            Long variantId,
            List<MultipartFile> files
    ) {

        Product product =
                loadAuthorizedProduct(productId);

        ProductVariant variant =
                productVariantService.getVariant(
                        product.getId(),
                        variantId
                );

        return variantImageService.addImages(
                variant,
                files
        );
    }


    public void deleteImage(
            Long productId,
            Long variantId,
            Long imageId
    ) {
        loadAuthorizedProduct(productId);

        ProductVariant variant =
                productVariantService.getVariant(
                        productId,
                        variantId
                );

        variantImageService.deleteImage(
                variant,
                imageId
        );
    }



    private Product loadAuthorizedProduct(Long productId) {
        Product product = productService.getProduct(productId);
        authorizationService.requireProductAccess(product);

        return product;
    }
}