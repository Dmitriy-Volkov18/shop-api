package com.example.shopapi.card.entities;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.productVariant.entities.ProductVariant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "cart_id",
                                "variant_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cart_id",
            nullable = false
    )
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            nullable = false
    )
    private ProductVariant variant;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    public void changeQuantity(
            int quantity
    ) {
        if(quantity <= 0){
            throw new BadRequestException(
                    "Quantity must be greater than zero"
            );
        }

        this.quantity = quantity;
    }

    public void increaseQuantity(
            int amount
    ){
        changeQuantity(
                this.quantity + amount
        );
    }

}