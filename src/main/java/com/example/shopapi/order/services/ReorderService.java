package com.example.shopapi.order.services;

import com.example.shopapi.card.CartService;
import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.order.dto.ReorderResult;
import com.example.shopapi.order.dto.SkippedReorderItem;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.entities.CustomerOrderItem;
import com.example.shopapi.order.enums.ReorderSkipReason;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.enums.ProductVariantStatus;
import com.example.shopapi.productVariant.services.ProductVariantService;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReorderService {

    private final CartService cartService;
    private final ProductVariantService variantService;


    public ReorderResult reorder(
            CustomerOrder order,
            Cart cart
    ) {

        List<SkippedReorderItem> skipped =
                new ArrayList<>();


        for (CustomerOrderItem item : order.getItems()) {

            ProductVariant variant =
                    variantService.getById(
                            item.getVariant().getId()
                    );


            if (!canBeReordered(
                    variant,
                    item.getQuantity()
            )) {

                skipped.add(
                        buildSkippedItem(
                                item,
                                variant
                        )
                );

                continue;
            }


            cartService.addItem(
                    cart,
                    variant,
                    item.getQuantity()
            );
        }


        return new ReorderResult(
                null,
                skipped
        );
    }


    private boolean canBeReordered(
            ProductVariant variant,
            int quantity
    ) {
        return variant.getStatus()
                == ProductVariantStatus.ACTIVE
                &&
                variant.hasEnoughStock(quantity);
    }


    private SkippedReorderItem buildSkippedItem(
            CustomerOrderItem item,
            ProductVariant variant
    ) {

        ReorderSkipReason reason;


        if (variant.getStatus()
                != ProductVariantStatus.ACTIVE) {

            reason = ReorderSkipReason.PRODUCT_INACTIVE;

        } else {

            reason = ReorderSkipReason.OUT_OF_STOCK;
        }


        return new SkippedReorderItem(
                variant.getId(),
                item.getProductName(),
                item.getSku(),
                item.getQuantity(),
                reason
        );
    }
}