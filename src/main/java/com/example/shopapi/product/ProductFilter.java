package com.example.shopapi.product;

import com.example.shopapi.product.enums.ProductSort;
import com.example.shopapi.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductFilter {

    private String search;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private Long categoryId;
    private Long userId;

    private ProductStatus status;
    private String brand;
    private String sku;
    private BigDecimal minRating;
    private Boolean inStock;
    private Boolean discounted;

    private ProductSort sort = ProductSort.NEWEST;
    private Map<String, List<String>> attributes = new HashMap<>();
}