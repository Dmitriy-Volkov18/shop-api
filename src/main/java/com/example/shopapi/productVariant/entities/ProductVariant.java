package com.example.shopapi.productVariant.entities;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.discounts.productDiscounts.ProductDiscount;
import com.example.shopapi.productVariant.enums.ProductVariantStatus;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.common.exception.InsufficientStockException;
import com.example.shopapi.discounts.interfaces.DiscountOwner;
import com.example.shopapi.product.entities.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "product_variants",
        indexes = {
                @Index(name = "idx_variant_product", columnList = "product_id"),
                @Index(name = "idx_variant_sku", columnList = "sku", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends BaseEntity implements DiscountOwner<ProductDiscount> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 100)
    private String sku;

    /**
     * Если null —
     * используется Product.price
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Embedded
    private VariantDimensions dimensions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductVariantStatus status = ProductVariantStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @OneToMany(
            mappedBy = "variant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("id ASC")
    private List<VariantAttribute> attributes = new ArrayList<>();

    @OneToMany(
            mappedBy = "variant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VariantImage> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "variant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductDiscount> discounts =
            new ArrayList<>();

    @Column(nullable = false)
    private int reservedQuantity = 0;

    @Transient
    public int getAvailableQuantity() {
        return Math.max(0, stockQuantity - reservedQuantity);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero"
            );
        }
    }

    public void reserve(int quantity){
        validateQuantity(quantity);

        if(getAvailableQuantity() < quantity){
            throw new InsufficientStockException(id);
        }

        reservedQuantity += quantity;

        recalcStockStatus();
    }

    public void releaseReservation(
            int quantity
    ){
        validateQuantity(quantity);

        if(reservedQuantity < quantity){
            throw new IllegalStateException(
                    "Not enough reserved stock"
            );
        }

        reservedQuantity -= quantity;

        recalcStockStatus();
    }

    public void confirmReservation(
            int quantity
    ){
        if(reservedQuantity < quantity){
            throw new IllegalStateException(
                    "Not enough reserved stock"
            );
        }

        reservedQuantity -= quantity;
        stockQuantity -= quantity;

        recalcStockStatus();
    }

    @Override
    public void addDiscount(
            ProductDiscount discount
    ){
        discounts.add(discount);
        discount.setVariant(this);
    }

    public void removeDiscount(
            ProductDiscount discount
    ) {
        discounts.remove(discount);
        discount.setVariant(null);
    }

    public void addImage(
            VariantImage image
    ) {
        images.add(image);
        image.setVariant(this);
    }

    public void removeImage(
            VariantImage image
    ) {
        images.remove(image);
        image.setVariant(null);
    }

    public void addAttribute(
            VariantAttribute attribute
    ) {
        attributes.add(attribute);
        attribute.setVariant(this);
    }

    public void removeAttribute(
            VariantAttribute attribute
    ) {
        attributes.remove(attribute);
        attribute.setVariant(null);
    }

    public void clearAttributes() {
        for (VariantAttribute attribute : new ArrayList<>(attributes)) {
            removeAttribute(attribute);
        }
    }

    public void increaseStock(
            int quantity
    ) {
        validateQuantity(quantity);

        stockQuantity += quantity;

        recalcStockStatus();
    }

    public void decreaseStock(
            int quantity
    ) {
        validateQuantity(quantity);

        if (stockQuantity < quantity) {
            throw new InsufficientStockException(id);
        }

        stockQuantity -= quantity;

        recalcStockStatus();
    }

    public boolean hasEnoughStock(int quantity) {
        return quantity > 0 && stockQuantity >= quantity;
    }

    public void activate() {
        status = ProductVariantStatus.ACTIVE;
    }

    public void deactivate() {
        status = ProductVariantStatus.INACTIVE;
    }

    public void markOutOfStock() {
        status = ProductVariantStatus.OUT_OF_STOCK;
    }

    public void recalcStockStatus() {
        if (getAvailableQuantity() == 0) {
            status = ProductVariantStatus.OUT_OF_STOCK;
        } else if (status == ProductVariantStatus.OUT_OF_STOCK) {
            status = ProductVariantStatus.ACTIVE;
        }
    }
}