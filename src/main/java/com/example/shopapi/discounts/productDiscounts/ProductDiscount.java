package com.example.shopapi.discounts.productDiscounts;

import com.example.shopapi.discounts.entities.AbstractDiscount;
import com.example.shopapi.productVariant.entities.ProductVariant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "product_discounts",
        indexes = {
                @Index(
                        name = "idx_discount_variant",
                        columnList = "variant_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ProductDiscount
        extends AbstractDiscount {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            nullable = false
    )
    private ProductVariant variant;

}