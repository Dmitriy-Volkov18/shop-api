package com.example.shopapi.card;

import com.example.shopapi.card.dto.CartItemResponse;
import com.example.shopapi.card.dto.CartResponse;
import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        uses = {
                ProductImageService.class,
        }
)
public interface CartMapper {

    @Mapping(
            target = "variantId",
            source = "variant.id"
    )
    @Mapping(
            target = "productName",
            source = "variant.product.name"
    )
    @Mapping(
            target = "sku",
            source = "variant.sku"
    )
    @Mapping(
            target = "image",
            source = "variant",
            qualifiedByName = "mainVariantImage"
    )

    @Mapping(
            target = "subtotal",
            ignore = true
    )
    CartItemResponse toItemResponse(
            CartItem item
    );

    CartResponse toResponse(
            Cart cart,
            Integer totalItems,
            BigDecimal totalPrice
    );
}