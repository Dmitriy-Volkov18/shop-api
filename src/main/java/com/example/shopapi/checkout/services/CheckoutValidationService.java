package com.example.shopapi.checkout.services;

import com.example.shopapi.card.CartService;
import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.productVariant.enums.ProductVariantStatus;
import com.example.shopapi.common.exception.businessExceptions.BusinessException;
import com.example.shopapi.common.exception.businessExceptions.EmptyCartException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutValidationService {

    private final CartService cartService;

    public void validate(Cart cart) {
        validateCart(cart);
        validateProductAvailability(cart);
    }

    private void validateCart(Cart cart) {
        if (cartService.isEmpty(cart)) {
            throw new EmptyCartException();
        }
    }

    private void validateProductAvailability(Cart cart) {
        for (CartItem item : cart.getItems()) {

            ProductVariant variant = item.getVariant();

            if (variant.getProduct().getStatus() != ProductStatus.ACTIVE) {
                throw new BusinessException(
                        "Product is unavailable: "
                                + variant.getProduct().getName()
                );
            }

            if (variant.getStatus() != ProductVariantStatus.ACTIVE) {
                throw new BusinessException(
                        "Variant is unavailable: "
                                + variant.getSku()
                );
            }
        }
    }
}