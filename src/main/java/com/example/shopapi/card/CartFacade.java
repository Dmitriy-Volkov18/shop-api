package com.example.shopapi.card;

import com.example.shopapi.card.dto.AddCartItemRequest;
import com.example.shopapi.card.dto.CartResponse;
import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.order.dto.UpdateCartItemRequest;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.common.exception.businessExceptions.InsufficientStockException;
import com.example.shopapi.productVariant.services.ProductVariantService;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartFacade {

    private final CartService cartService;
    private final ProductVariantService productVariantService;
    private final CurrentUserService currentUserService;
    private final CartResponseBuilder cartResponseBuilder;

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        Cart cart = currentOrCreateCart();

        return cartResponseBuilder.build(cart);
    }

    public CartResponse addItem(
            AddCartItemRequest request
    ) {
        validateQuantity(request.quantity());

        Cart cart = currentOrCreateCart();

        ProductVariant variant =
                productVariantService.getById(
                        request.variantId()
                );

        CartItem item =
                cartService.getOrCreateItem(
                        cart,
                        variant
                );

        int newQuantity = item.getQuantity() + request.quantity();
        item.setQuantity(newQuantity);

        validateStock(variant, newQuantity);

        cartService.save(cart);

        log.info("Cart item is added");

        return cartResponseBuilder.build(cart);
    }

    public CartResponse updateItem(
            Long itemId,
            UpdateCartItemRequest request
    ) {
        validateQuantity(request.quantity());

        Cart cart = currentCart();
        CartItem item = cartService.getItem(itemId);

        validateItemOwner(item, cart);

        validateStock(
                item.getVariant(),
                request.quantity()
        );

        item.setQuantity(request.quantity());

        cartService.save(cart);

        log.info("Cart item is updated");

        return cartResponseBuilder.build(cart);
    }

    public CartResponse removeItem(
            Long itemId
    ) {
        Cart cart = currentCart();
        CartItem item = cartService.getItem(itemId);

        validateItemOwner(item, cart);
        cart.removeItem(item);

        cartService.save(cart);

        log.info("Cart item is removed");

        return cartResponseBuilder.build(cart);
    }

    public CartResponse clearCart() {
        Cart cart = currentCart();
        cart.clearItems();

        cartService.save(cart);

        log.info("Cart is cleared");

        return cartResponseBuilder.build(cart);
    }

    private User currentUser() {
        return currentUserService.getCurrentUserEntity();
    }

    private Cart currentCart() {
        return cartService.getByUser(
                currentUser()
        );
    }

    private Cart currentOrCreateCart() {
        return cartService.getOrCreateCart(
                currentUser()
        );
    }

    private void validateStock(
            ProductVariant variant,
            int quantity
    ) {
        if (!variant.hasEnoughStock(quantity)) {
            throw new InsufficientStockException(
                    variant.getId()
            );
        }
    }

    private void validateQuantity(
            Integer quantity
    ) {
        if (quantity == null || quantity < 1) {
            throw new BadRequestException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private void validateItemOwner(
            CartItem item,
            Cart cart
    ) {
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException(
                    "Cart item does not belong to user"
            );
        }
    }
}