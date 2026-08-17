package com.example.shopapi.product;

import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.dto.ProductImageResponse;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.services.ImageUploadService;
import com.example.shopapi.product.services.ProductImageService;
import com.example.shopapi.product.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageFacade {

    private final ProductService productService;
    private final AuthorizationService authorizationService;
    private final ProductImageService productImageService;
    private final ImageUploadService imageUploadService;

    public List<ProductImageResponse> getImages(Long productId) {
        Product product = productService.getProduct(productId);

        return productImageService.getImages(product);
    }

    public ProductImageResponse uploadImage(
            Long productId,
            MultipartFile file,
            boolean primary
    ){
        Product product = loadAuthorizedProduct(productId);
        ImageMetadata metadata = imageUploadService.process(file);

        log.info("Product image is uploaded");

        return productImageService.addImage(
                product,
                metadata,
                primary
        );
    }

    public List<ProductImageResponse> uploadImages(
            Long productId,
            List<MultipartFile> files
    ){
        Product product = loadAuthorizedProduct(productId);

        log.info("Product images are uploaded");

        return productImageService
                .addImages(
                        product,
                        files
                );
    }

    public void deleteImage(
            Long productId,
            Long imageId
    ){
        Product product = loadAuthorizedProduct(productId);

        productImageService.deleteImage(
                product,
                imageId
        );

        log.info("Product image is deleted");
    }

    private Product loadAuthorizedProduct(Long productId) {
        Product product = productService.getProduct(productId);
        authorizationService.requireProductAccess(product);

        return product;
    }
}