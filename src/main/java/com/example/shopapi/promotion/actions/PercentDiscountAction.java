package com.example.shopapi.promotion.actions;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.engine.PromotionResult;
import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.interfaces.PromotionAction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentDiscountAction
        implements PromotionAction {


    @Override
    public PromotionActionType getType() {

        return PromotionActionType.PERCENT_DISCOUNT;
    }


    @Override
    public PromotionResult apply(
            Promotion promotion,
            PromotionContext context
    ) {

        BigDecimal cartTotal =
                context.getCartTotal();

        BigDecimal discount =
                cartTotal
                        .multiply(
                                promotion.getActionValue()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );


        return PromotionResult.fixedDiscount(
                discount
        );
    }
}