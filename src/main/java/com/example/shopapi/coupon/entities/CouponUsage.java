package com.example.shopapi.coupon.entities;

import com.example.shopapi.user.entities.User;
import com.example.shopapi.order.entities.CustomerOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon_usages",
        indexes = {

                @Index(
                        name = "idx_coupon_usage_coupon",
                        columnList = "coupon_id"
                ),

                @Index(
                        name = "idx_coupon_usage_user",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_coupon_usage_order",
                        columnList = "order_id"
                ),

                @Index(
                        name = "idx_coupon_usage_coupon_user",
                        columnList = "coupon_id,user_id"
                ),

                @Index(
                        name = "idx_coupon_usage_date",
                        columnList = "usedAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CustomerOrder order;

    @Column(nullable = false)
    private LocalDateTime usedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;
}