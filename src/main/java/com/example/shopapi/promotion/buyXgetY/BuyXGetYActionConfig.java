package com.example.shopapi.promotion.buyXgetY;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.promotion.buyXgetY.enums.RewardType;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyXGetYActionConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "promotion_id",
            nullable = false,
            unique = true
    )
    private Promotion promotion;

    @Column(nullable = false)
    private Integer buyQuantity;

    @Column(nullable = false)
    private Integer rewardQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionTargetType buyTargetType;

    @Column(nullable = false)
    private Long buyTargetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionTargetType rewardTargetType;

    @Column()
    private Long rewardTargetId;

    @Column(nullable = false)
    private BigDecimal rewardValue;

}