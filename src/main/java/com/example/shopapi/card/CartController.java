package com.example.shopapi.card;

import com.example.shopapi.card.dto.AddCartItemRequest;
import com.example.shopapi.card.dto.CartResponse;
import com.example.shopapi.order.dto.UpdateCartItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    @GetMapping
    public CartResponse getCart() {
        return cartFacade.getCart();
    }

    @PostMapping("/items")
    public CartResponse addItem(
            @Valid
            @RequestBody AddCartItemRequest request
    ) {
        return cartFacade.addItem(request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(
            @PathVariable Long itemId,

            @Valid
            @RequestBody UpdateCartItemRequest request
    ) {
        return cartFacade.updateItem(
                itemId,
                request
        );
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(
            @PathVariable Long itemId
    ) {
        return cartFacade.removeItem(itemId);
    }

    @DeleteMapping
    public CartResponse clearCart() {
        return cartFacade.clearCart();
    }
}