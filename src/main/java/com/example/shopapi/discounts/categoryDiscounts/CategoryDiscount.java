package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.category.Category;
import com.example.shopapi.discounts.entities.AbstractDiscount;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "category_discounts",
        indexes = {
                @Index(
                        name = "idx_category_discount_category",
                        columnList = "category_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CategoryDiscount extends AbstractDiscount {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

}