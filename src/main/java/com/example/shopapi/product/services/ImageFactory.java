package com.example.shopapi.product.services;

import com.example.shopapi.common.AbstractImage;
import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.entities.ProductImage;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.entities.VariantImage;
import com.example.shopapi.reviews.entities.Review;
import com.example.shopapi.reviews.entities.ReviewImage;
import org.springframework.stereotype.Component;

@Component
public class ImageFactory {

    public ProductImage createProductImage(
            Product product,
            ImageMetadata metadata,
            boolean primary
    ) {
        ProductImage image = new ProductImage();

        fillMetadata(image, metadata);

        image.setPrimaryImage(primary);
        image.setSortOrder(product.getImages().size());

        return image;
    }

    public VariantImage createVariantImage(
            ProductVariant variant,
            ImageMetadata metadata,
            boolean primary
    ) {
        VariantImage image = new VariantImage();

        fillMetadata(image, metadata);

        image.setPrimaryImage(primary);
        image.setSortOrder(variant.getImages().size());

        return image;
    }

    public ReviewImage createReviewImage(
            Review review,
            ImageMetadata metadata
    ) {
        ReviewImage image = new ReviewImage();

        fillMetadata(image, metadata);

        image.setReview(review);
        image.setSortOrder(review.getImages().size());

        return image;
    }

    private void fillMetadata(
            AbstractImage image,
            ImageMetadata metadata
    ) {
        image.setImageUrl( metadata.storagePath());
        image.setFileName(metadata.fileName());
        image.setContentType(metadata.contentType());
        image.setFileSize(metadata.fileSize());
        image.setWidth(metadata.width());
        image.setHeight(metadata.height());
    }
}