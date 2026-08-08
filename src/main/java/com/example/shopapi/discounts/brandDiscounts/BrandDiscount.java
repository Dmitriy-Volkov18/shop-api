package com.example.shopapi.discounts.brandDiscounts;

import com.example.shopapi.brand.Brand;
import com.example.shopapi.discounts.entities.AbstractDiscount;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
        name = "brand_discounts",
        indexes = {
                @Index(
                        name = "idx_brand_discount_brand",
                        columnList = "brand_id"
                ),
                @Index(
                        name = "idx_brand_discount_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDiscount extends AbstractDiscount {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "brand_id",
            nullable = false
    )
    private Brand brand;
}