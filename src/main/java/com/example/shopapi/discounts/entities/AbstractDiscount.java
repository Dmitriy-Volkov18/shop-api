package com.example.shopapi.discounts.entities;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected DiscountType type;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    protected BigDecimal discountValue;

    @Column(length = 200)
    protected String description;

    @Column(nullable = false)
    protected LocalDateTime startsAt;

    @Column(nullable = false)
    protected LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected DiscountStatus status = DiscountStatus.ACTIVE;

    @Column(nullable = false)
    protected Integer priority = 0;

    @Column(nullable = false)
    protected Integer applicationOrder = 0;

    @Column(nullable = false)
    protected boolean stackable = true;

    @Column(nullable = false)
    protected boolean exclusive = false;

    public boolean isActive() {
        return status == DiscountStatus.ACTIVE;
    }

    public boolean isRunning(
            LocalDateTime now
    ) {
        return !startsAt.isAfter(now) && !endsAt.isBefore(now);
    }

    public boolean isApplicable(
            LocalDateTime now
    ) {
        return isActive() && isRunning(now);
    }

    public boolean hasPriorityOver(
            AbstractDiscount other
    ) {
        return priority > other.priority;
    }
}
