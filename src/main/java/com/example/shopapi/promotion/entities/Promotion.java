package com.example.shopapi.promotion.entities;

import com.example.shopapi.promotion.buyXgetY.BuyXGetYActionConfig;
import com.example.shopapi.promotion.campaign.PromotionCampaign;
import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.enums.PromotionStatus;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "promotions",
        indexes = {
                @Index(
                        name = "idx_promotion_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_promotion_period",
                        columnList = "startsAt, endsAt"
                ),
                @Index(
                        name = "idx_promotion_priority",
                        columnList = "priority"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private Integer priority = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionRuleType ruleType;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal ruleValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionActionType actionType;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal actionValue;

    @Column(nullable = false)
    private boolean exclusive = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private PromotionCampaign campaign;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionTargetType targetType;

    @Column
    private Long targetId;

    @Column(nullable = false)
    private boolean stopFurtherProcessing = false;

    @OneToOne(
            mappedBy = "promotion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private BuyXGetYActionConfig buyXGetYConfig;


    public boolean isActive() {

        return status == PromotionStatus.ACTIVE;
    }

    public boolean isRunning(
            LocalDateTime now
    ) {

        return !startsAt.isAfter(now)
                &&
                !endsAt.isBefore(now);
    }

    public boolean isApplicable(
            LocalDateTime now
    ) {

        return isActive()
                &&
                isRunning(now);
    }

    public boolean hasCampaign() {

        return campaign != null;
    }

    public boolean shouldStopProcessing() {

        return stopFurtherProcessing;
    }

}