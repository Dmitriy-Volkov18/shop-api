package com.example.shopapi.coupon.entities;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupons",
        indexes = {

                @Index(
                        name = "idx_coupon_code",
                        columnList = "code",
                        unique = true
                ),

                @Index(
                        name = "idx_coupon_status",
                        columnList = "status"
                )

        }
)
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal discountValue;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountStatus status = DiscountStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal minimumOrderAmount;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal maximumDiscountAmount;

    private Integer usageLimit;

    @Column(nullable = false)
    private Integer usedCount = 0;

    private Integer perUserLimit;

    @Column(nullable = false)
    private Boolean stackable = false;

    public boolean isActive() {
        return status == DiscountStatus.ACTIVE;
    }

    public boolean isPercent() {
        return type == DiscountType.PERCENT;
    }

    public boolean isRunning(LocalDateTime now) {
        return !startsAt.isAfter(now) && !endsAt.isBefore(now);
    }

    public boolean isApplicable(LocalDateTime now) {
        return isActive() && isRunning(now);
    }

    public boolean hasUsageLimit() {
        return usageLimit != null;
    }

    public boolean canBeUsed() {
        if (!hasUsageLimit()) {
            return true;
        }

        return usedCount < usageLimit;
    }

    public boolean hasPerUserLimit() {
        return perUserLimit != null;
    }

    public boolean canBeUsedByUser(
            long currentUses
    ) {
        if (!hasPerUserLimit()) {
            return true;
        }

        return currentUses < perUserLimit;
    }

    public boolean hasMinimumOrder() {
        return minimumOrderAmount != null;
    }

    public boolean satisfiesMinimumOrder(
            BigDecimal amount
    ) {
        if (!hasMinimumOrder()) {
            return true;
        }

        return amount.compareTo(minimumOrderAmount) >= 0;
    }

    public boolean hasMaximumDiscount() {
        return maximumDiscountAmount != null;
    }

    public BigDecimal limitDiscount(
            BigDecimal discount
    ) {
        if (!hasMaximumDiscount()) {
            return discount;
        }

        return discount.min(maximumDiscountAmount);
    }

    public void increaseUsage() {
        usedCount++;
    }

    public void registerUsage() {

        if (!canBeUsed()) {
            throw new IllegalStateException(
                    "Coupon usage limit exceeded"
            );
        }

        increaseUsage();
    }

    public BigDecimal calculateDiscount(
        BigDecimal orderTotal
    ) {
        BigDecimal discount =
                isPercent()
                        ? orderTotal
                        .multiply(discountValue)
                        .divide(BigDecimal.valueOf(100))
                        : discountValue;

        discount = limitDiscount(discount);

        return discount.min(orderTotal);
    }
}