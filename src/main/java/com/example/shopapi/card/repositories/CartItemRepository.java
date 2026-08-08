package com.example.shopapi.card.repositories;

import com.example.shopapi.card.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndVariantId(
            Long cartId,
            Long variantId
    );

    void deleteByCartId(Long cartId);

}