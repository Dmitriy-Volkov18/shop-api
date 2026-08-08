package com.example.shopapi.brand;

import com.example.shopapi.common.validation.ValidationUtils;
import org.springframework.stereotype.Service;

@Service
public class BrandValidationService {
    public void validate(
            Brand brand
    ) {
        validateName(
                brand
        );
    }

    private void validateName(
            Brand brand
    ) {
        ValidationUtils.requireNotBlank(
                brand.getName(),
                "Brand name cannot be empty"
        );
    }
}