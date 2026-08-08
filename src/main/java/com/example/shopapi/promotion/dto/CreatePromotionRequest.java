package com.example.shopapi.promotion.dto;

import com.example.shopapi.promotion.buyXgetY.dto.BuyXGetYConfigRequest;
import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePromotionRequest(

        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull
        LocalDateTime startsAt,

        @NotNull
        LocalDateTime endsAt,

        @NotNull
        @PositiveOrZero
        Integer priority,

        @NotNull
        PromotionRuleType ruleType,

        @PositiveOrZero
        BigDecimal ruleValue,

        @NotNull
        PromotionActionType actionType,

        @PositiveOrZero
        BigDecimal actionValue,

        PromotionTargetType targetType,

        Long targetId,

        boolean stopFurtherProcessing,

        BuyXGetYConfigRequest buyXGetYConfig
) {
}