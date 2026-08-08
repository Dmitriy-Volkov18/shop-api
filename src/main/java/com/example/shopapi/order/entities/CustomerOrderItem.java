package com.example.shopapi.order.entities;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.productVariant.entities.ProductVariant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "customer_order_items",
        indexes = {
                @Index(
                        name = "idx_order_item_variant",
                        columnList = "variant_id"
                ),
                @Index(
                        name = "idx_order_item_order",
                        columnList = "order_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            nullable = false
    )
    private ProductVariant variant;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    public void calculateTotalPrice() {
        this.totalPrice =
                unitPrice.multiply(
                        BigDecimal.valueOf(quantity)
                );
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public void changeQuantity(int quantity) {

        if (quantity <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero"
            );
        }

        this.quantity = quantity;

        calculateTotalPrice();
    }

    public void changeUnitPrice(
            BigDecimal unitPrice
    ) {
        this.unitPrice = unitPrice;

        calculateTotalPrice();
    }
}