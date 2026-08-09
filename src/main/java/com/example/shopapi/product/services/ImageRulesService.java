package com.example.shopapi.product.services;

import com.example.shopapi.common.AbstractImage;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.common.interfaces.PrimaryImage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageRulesService {

    public void validateImageCount(
            int currentCount,
            int addingCount,
            int maxImages
    ) {
        if (currentCount + addingCount > maxImages) {
            throw new BadRequestException(
                    "Maximum " + maxImages + " images allowed"
            );
        }
    }

    public void normalizeSortOrder(
            List<? extends AbstractImage> images
    ) {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setSortOrder(i);
        }
    }

    public void validateSinglePrimary(
            List<? extends PrimaryImage> images
    ) {
        long primaryCount =
                images.stream()
                        .filter(PrimaryImage::isPrimaryImage)
                        .count();

        if (primaryCount > 1) {
            throw new BadRequestException(
                    "Only one primary image is allowed"
            );
        }
    }
}