package com.example.shopapi.promotion.interfaces;

import com.example.shopapi.promotion.entities.Promotion;

import java.util.List;

public interface PromotionCombinationStrategy {
    List<Promotion> combine(
            List<Promotion> promotions
    );

}