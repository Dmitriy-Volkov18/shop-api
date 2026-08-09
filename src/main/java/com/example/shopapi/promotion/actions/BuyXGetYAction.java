package com.example.shopapi.promotion.actions;

import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductBasePriceService;
import com.example.shopapi.promotion.buyXgetY.BuyXGetYActionConfig;
import com.example.shopapi.promotion.buyXgetY.BuyXGetYActionConfigRepository;
import com.example.shopapi.promotion.buyXgetY.BuyXGetYRewardResolver;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.engine.PromotionDiscountLine;
import com.example.shopapi.promotion.engine.PromotionResult;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.interfaces.PromotionAction;
import com.example.shopapi.promotion.services.CartItemFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@RequiredArgsConstructor
public class BuyXGetYAction implements PromotionAction {

    private final ProductBasePriceService basePriceService;
    private final BuyXGetYRewardResolver rewardResolver;
    private final BuyXGetYActionConfigRepository repository;
    private final CartItemFinder cartItemFinder;

    @Override
    public PromotionActionType getType() {
        return PromotionActionType.BUY_X_GET_Y;
    }

    public PromotionResult apply(
            Promotion promotion,
            PromotionContext context
    ) {
        BuyXGetYActionConfig config = loadConfiguration(promotion);

        List<CartItem> candidates =
                cartItemFinder.findItems(
                        context.getCart(),
                        promotion.getTargetType(),
                        promotion.getTargetId()
                );

        if(!hasEnoughItems(
                candidates,
                config
        )) {
            return PromotionResult.empty();
        }

        List<CartItem> rewardItems =
                rewardResolver.resolve(
                        context,
                        config
                );

        if(rewardItems.isEmpty()) {
            return PromotionResult.empty();
        }

        List<PromotionDiscountLine> lines =
                buildDiscountLines(
                        candidates,
                        rewardItems,
                        config
                );

        if(lines.isEmpty()) {
            return PromotionResult.empty();
        }

        return PromotionResult.withDiscountLines(
                lines
        );
    }


    private List<PromotionDiscountLine> buildDiscountLines(
            List<CartItem> candidates,
            List<CartItem> rewardItems,
            BuyXGetYActionConfig config
    ) {
        int freeQuantity =
                calculateFreeQuantity(
                        candidates,
                        config
                );

        if (freeQuantity <= 0) {
            return List.of();
        }

        AtomicInteger remaining =
                new AtomicInteger(
                        freeQuantity
                );

        return rewardItems.stream()
                .sorted(
                        Comparator.comparing(
                                item ->
                                        basePriceService.getBasePrice(
                                                item.getVariant()
                                        )
                        )
                )

                .map(item -> {
                    int quantity =
                            Math.min(
                                    item.getQuantity(),
                                    remaining.get()
                            );


                    remaining.addAndGet(-quantity);

                    if(quantity <= 0) {
                        return null;
                    }

                    return new PromotionDiscountLine(
                            item.getVariant().getId(),
                            quantity,
                            calculateDiscount(
                                    item,
                                    quantity,
                                    config
                            )
                    );

                })
                .filter(Objects::nonNull)
                .toList();
    }

    private BigDecimal calculateDiscount(
            CartItem item,
            int quantity,
            BuyXGetYActionConfig config
    ) {
        BigDecimal price =
                basePriceService.getBasePrice(
                        item.getVariant()
                );

        BigDecimal totalPrice =
                price.multiply(
                        BigDecimal.valueOf(quantity)
                );


        return switch(config.getRewardType()) {
            case FREE ->
                    totalPrice;

            case PERCENT_DISCOUNT ->
                    totalPrice
                            .multiply(
                                    config.getRewardValue()
                            )
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            case FIXED_DISCOUNT ->
                    config.getRewardValue()
                            .multiply(
                                    BigDecimal.valueOf(quantity)
                            )
                            .min(totalPrice);
        };
    }

    private int calculateFreeQuantity(
            List<CartItem> candidates,
            BuyXGetYActionConfig config
    )
    {
        int boughtQuantity =
                candidates.stream()
                        .mapToInt(
                                CartItem::getQuantity
                        )
                        .sum();

        return
                (boughtQuantity / config.getBuyQuantity())
                        *
                        config.getRewardQuantity();
    }


    private BuyXGetYActionConfig loadConfiguration(
            Promotion promotion
    ) {
        return repository.findByPromotionId(
                        promotion.getId()
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "BuyXGetY configuration not found for promotion "
                                        + promotion.getId()
                        )
                );
    }

    private boolean hasEnoughItems(
            List<CartItem> items,
            BuyXGetYActionConfig config
    ) {
        int totalQuantity =
                items.stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();

        return totalQuantity >= config.getBuyQuantity();
    }

}