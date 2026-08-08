package com.example.shopapi.product.entities;

import com.example.shopapi.brand.Brand;
import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.category.Category;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_status", columnList = "status"),
                @Index(name = "idx_product_category", columnList = "category_id"),
                @Index(name = "idx_product_user", columnList = "user_id"),
                @Index(name = "idx_product_price", columnList = "price")
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "brand_id",
            nullable = false
    )
    private Brand brand;

    @Column(length = 5000)
    private String description;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer salesCount = 0;

    @Column(nullable = false)
    private Integer wishlistCount = 0;

    @Column(nullable = false)
    private Integer viewCount = 0;

    public void increaseSales(int quantity) {
        salesCount += quantity;
    }

    public void increaseWishlist() {
        wishlistCount++;
    }

    public void decreaseWishlist() {
        if (wishlistCount > 0) {
            wishlistCount--;
        }
    }

    public void increaseViews() {
        viewCount++;
    }


    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("id ASC")
    private List<ProductVariant> variants = new ArrayList<>();


    public void updateRating(
            int reviewCount,
            BigDecimal averageRating
    ) {

        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }

    public boolean hasReviews() {
        return reviewCount > 0;
    }

    public void clearRating() {

        reviewCount = 0;
        averageRating = BigDecimal.ZERO;
    }

    public void addImage(ProductImage image) {

        image.setProduct(this);

        images.add(image);
    }

    public void removeImage(ProductImage image) {

        images.remove(image);

        image.setProduct(null);
    }

    public void clearImages() {

        images.clear();
    }

    @Transactional
    public void decreaseStock(int qty) {

        if (qty <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        if (stockQuantity < qty) {
            throw new BadRequestException("Not enough stock");
        }

        stockQuantity -= qty;
        recalcStockStatus();
    }

    @Transactional
    public void increaseStock(int qty) {

        if (qty <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        stockQuantity += qty;
        recalcStockStatus();
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    public void markOutOfStock() {
        this.status = ProductStatus.OUT_OF_STOCK;
    }

    public void recalcStockStatus() {

        if (stockQuantity == 0) {
            status = ProductStatus.OUT_OF_STOCK;
        } else if (status == ProductStatus.OUT_OF_STOCK) {
            status = ProductStatus.ACTIVE;
        }
    }

    public void addVariant(ProductVariant variant) {

        variants.add(variant);

        variant.setProduct(this);
    }

    public void removeVariant(ProductVariant variant) {

        variants.remove(variant);

        variant.setProduct(null);
    }
}