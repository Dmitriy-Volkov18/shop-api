package com.example.shopapi.promotion.targets;

import com.example.shopapi.category.Category;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import com.example.shopapi.promotion.interfaces.PromotionTarget;
import org.springframework.stereotype.Component;

@Component
public class CategoryPromotionTarget implements PromotionTarget {

    @Override
    public PromotionTargetType getType() {
        return PromotionTargetType.CATEGORY;
    }

    @Override
    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        ProductVariant variant = context.getVariant();

        if (variant == null) {
            return false;
        }

        Category category = variant.getProduct().getCategory();

        if (category == null) {
            return false;
        }

        return category.getId()
                .equals(
                        promotion.getTargetId()
                );
    }
}