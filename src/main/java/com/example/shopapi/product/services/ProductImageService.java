package com.example.shopapi.product.services;

import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.dto.ProductImageResponse;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.entities.ProductImage;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.entities.VariantImage;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.product.mappers.ProductImageMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.shopapi.common.constants.ProductConstants.MAX_PRODUCT_IMAGES;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageMapper productImageMapper;
    private final ImageUploadService imageUploadService;
    private final ImageRulesService rules;
    private final ImageFactory imageFactory;

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImages(Product product) {
        return product.getImages()
                .stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProductImageResponse addImage(
            Product product,
            ImageMetadata metadata,
            boolean primaryImage
    ) {
        rules.validateImageCount(
                product.getImages().size(),
                1,
                MAX_PRODUCT_IMAGES
        );

        ProductImage image =
                imageFactory.createProductImage(
                        product,
                        metadata,
                        primaryImage
                );

        product.addImage(image);

        rules.validateSinglePrimary(product.getImages());

        return productImageMapper.toResponse(image);
    }

    @Transactional
    public List<ProductImageResponse> addImages(
            Product product,
            List<MultipartFile> files
    ){
        rules.validateImageCount(
                product.getImages().size(),
                files.size(),
                MAX_PRODUCT_IMAGES
        );

        List<ProductImageResponse> result = new ArrayList<>();

        boolean firstPrimary = product.getImages().isEmpty();

        for (int i = 0; i < files.size(); i++) {
            ImageMetadata metadata = imageUploadService.process(files.get(i));

            ProductImage image =
                    imageFactory.createProductImage(
                            product,
                            metadata,
                            firstPrimary && i == 0
                    );

            product.addImage(image);

            result.add(productImageMapper.toResponse(image));
        }

        rules.validateSinglePrimary(product.getImages());

        return result;
    }

    @Transactional
    public void deleteImage(
            Product product,
            Long imageId
    ) {
        if (product.getImages().size() == 1) {
            throw new BadRequestException(
                    "Product must contain at least one image"
            );
        }

        ProductImage image = findImage(product, imageId);

        boolean wasPrimary = image.isPrimaryImage();

        product.removeImage(image);

        if (wasPrimary) {
            product.getImages()
                    .getFirst()
                    .setPrimaryImage(true);
        }

        rules.normalizeSortOrder(product.getImages());
    }

    private ProductImage findImage(
            Product product,
            Long imageId
    ) {
        return product.getImages()
                .stream()
                .filter(image ->
                        image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() ->
                        new BadRequestException(
                                "Image not found"
                        ));
    }

    @Named("mainProductImage")
    @Transactional(readOnly = true)
    public String getMainImage(Product product) {
        if (product.getImages() == null) {
            return null;
        }

        return product.getImages()
                .stream()
                .filter(ProductImage::isPrimaryImage)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }

    @Named("mainVariantImage")
    @Transactional(readOnly = true)
    public String getMainImage(ProductVariant variant) {
        if (variant.getImages() == null) {
            return null;
        }

        return variant.getImages()
                .stream()
                .filter(VariantImage::isPrimaryImage)
                .findFirst()
                .map(VariantImage::getImageUrl)
                .orElse(null);
    }
}