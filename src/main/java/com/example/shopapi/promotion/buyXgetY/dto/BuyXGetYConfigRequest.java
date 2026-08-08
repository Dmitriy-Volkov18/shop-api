package com.example.shopapi.promotion.buyXgetY.dto;

import com.example.shopapi.promotion.buyXgetY.enums.RewardType;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BuyXGetYConfigRequest(

        @NotNull
        @Positive
        Integer buyQuantity,


        @NotNull
        @Positive
        Integer rewardQuantity,


        @NotNull
        RewardType rewardType,


        @NotNull
        PromotionTargetType rewardTargetType,


        Long rewardTargetId,


        @PositiveOrZero
        BigDecimal rewardValue

) {
}