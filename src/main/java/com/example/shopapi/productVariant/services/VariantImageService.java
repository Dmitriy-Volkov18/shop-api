package com.example.shopapi.productVariant.services;

import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.services.ImageUploadService;
import com.example.shopapi.productVariant.dto.VariantImageResponse;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.entities.VariantImage;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.productVariant.mappers.VariantImageMapper;
import com.example.shopapi.product.services.ImageFactory;
import com.example.shopapi.product.services.ImageRulesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantImageService {

    private static final int MAX_VARIANT_IMAGES = 10;

    private final VariantImageMapper mapper;
    private final ImageRulesService rules;
    private final ImageFactory imageFactory;
    private final ImageUploadService imageUploadService;

    @Transactional(readOnly = true)
    public List<VariantImageResponse> getImages(
            ProductVariant variant
    ) {
        return variant.getImages()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public VariantImageResponse addImage(
            ProductVariant variant,
            ImageMetadata metadata,
            boolean primaryImage
    ) {
        rules.validateImageCount(
                variant.getImages().size(),
                1,
                MAX_VARIANT_IMAGES
        );

        VariantImage image =
                imageFactory.createVariantImage(
                        variant,
                        metadata,
                        primaryImage
                );

        variant.addImage(image);

        rules.validateSinglePrimary(variant.getImages());

        return mapper.toResponse(image);
    }

    @Transactional
    public List<VariantImageResponse> addImages(
            ProductVariant variant,
            List<MultipartFile> files
    ) {
        rules.validateImageCount(
                variant.getImages().size(),
                files.size(),
                MAX_VARIANT_IMAGES
        );

        boolean firstPrimary = variant.getImages().isEmpty();

        List<VariantImageResponse> result = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            ImageMetadata metadata = imageUploadService.process(files.get(i));

            VariantImage image =
                    imageFactory.createVariantImage(
                            variant,
                            metadata,
                            firstPrimary && i == 0
                    );

            variant.addImage(image);

            result.add(mapper.toResponse(image));
        }

        rules.validateSinglePrimary(variant.getImages());

        return result;
    }

    @Transactional
    public void deleteImage(
            ProductVariant variant,
            Long imageId
    ) {
        if (variant.getImages().size() == 1) {
            throw new BadRequestException(
                    "Variant must contain at least one image"
            );
        }

        VariantImage image =
                findImage(
                        variant,
                        imageId
                );

        boolean wasPrimary = image.isPrimaryImage();

        variant.removeImage(image);

        if (wasPrimary) {
            variant.getImages()
                    .getFirst()
                    .setPrimaryImage(true);
        }

        rules.normalizeSortOrder(variant.getImages());
    }


    private VariantImage findImage(
            ProductVariant variant,
            Long imageId
    ) {
        return variant.getImages()
                .stream()
                .filter(i ->
                        i.getId().equals(imageId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new BadRequestException(
                                "Image not found"
                        )
                );
    }

}