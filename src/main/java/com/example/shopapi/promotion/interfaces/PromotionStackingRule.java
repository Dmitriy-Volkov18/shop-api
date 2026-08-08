package com.example.shopapi.promotion.interfaces;

import com.example.shopapi.promotion.engine.AppliedPromotion;
import com.example.shopapi.promotion.entities.Promotion;

import java.util.List;

public interface PromotionStackingRule {


    boolean isApplicable(
            Promotion candidate,
            List<Promotion> selected
    );

}