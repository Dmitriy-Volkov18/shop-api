package com.example.shopapi.product.dto;

import java.util.Map;
import java.util.Set;

public record RecommendationProfile(
        Map<Long, Integer> categoryWeights,
        Map<Long, Integer> brandWeights,
        Map<String,Integer> searchWeights,
        Set<Long> excludedProductIds
) {

    public boolean isEmpty() {
        return categoryWeights.isEmpty()
                && brandWeights.isEmpty()
                && searchWeights.isEmpty();
    }
}