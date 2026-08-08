package com.example.shopapi.promotion.buyXgetY;

import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductBasePriceService;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import com.example.shopapi.promotion.services.CartItemFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyXGetYRewardResolver {

    private final ProductBasePriceService basePriceService;
    private final CartItemFinder cartItemFinder;


    public List<CartItem> resolve(
            PromotionContext context,
            BuyXGetYActionConfig config
    ) {

        if (config.getRewardTargetType()
                == PromotionTargetType.SAME_PRODUCT) {

            return cartItemFinder.findItems(
                    context.getCart(),
                    config.getBuyTargetType(),
                    config.getBuyTargetId()
            );
        }


        return cartItemFinder.findItems(
                context.getCart(),
                config.getRewardTargetType(),
                config.getRewardTargetId()
        );
    }
}