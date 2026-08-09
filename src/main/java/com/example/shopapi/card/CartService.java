package com.example.shopapi.card;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.CartItemNotFoundException;
import com.example.shopapi.common.exception.CartNotFoundException;
import com.example.shopapi.card.repositories.CartItemRepository;
import com.example.shopapi.card.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public void addItem(
            Cart cart,
            ProductVariant variant,
            int quantity
    ) {
        CartItem item =
                getOrCreateItem(
                        cart,
                        variant
                );

        item.changeQuantity(item.getQuantity() + quantity);
    }


    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {

                    Cart cart = new Cart();
                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    public CartItem getOrCreateItem(
            Cart cart,
            ProductVariant variant
    ) {
        return findItemByVariant(
                cart.getId(),
                variant.getId()
        ).orElseGet(() -> {
            CartItem item = new CartItem();
            item.setVariant(variant);
            item.setQuantity(0);

            cart.addItem(item);

            return item;
        });
    }

    @Transactional(readOnly = true)
    public Cart getByUser(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new CartNotFoundException(user.getId()));
    }

    @Transactional(readOnly = true)
    public CartItem getItem(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException(itemId));
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> findItemByVariant(
            Long cartId,
            Long variantId
    ) {
        return cartItemRepository.findByCartIdAndVariantId(
                cartId,
                variantId
        );
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    public boolean isEmpty(Cart cart) {
        return cart.getItems().isEmpty();
    }

    public void clear(
            Cart cart
    ) {
        cart.clearItems();

        cartRepository.save(cart);
    }

}