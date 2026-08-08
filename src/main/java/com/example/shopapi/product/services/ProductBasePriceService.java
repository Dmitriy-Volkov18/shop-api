package com.example.shopapi.product.services;

import com.example.shopapi.productVariant.entities.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductBasePriceService {

    public BigDecimal getBasePrice(
            ProductVariant variant
    ) {
        return variant.getPrice();
    }
}