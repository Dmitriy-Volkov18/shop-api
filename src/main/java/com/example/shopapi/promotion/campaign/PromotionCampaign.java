package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "promotion_campaigns")
public class PromotionCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 150,
            unique = true
    )
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    @Builder.Default
    private List<Promotion> promotions = new ArrayList<>();

    public void addPromotion(
            Promotion promotion
    ) {

        promotions.add(
                promotion
        );

        promotion.setCampaign(
                this
        );
    }


    public void removePromotion(
            Promotion promotion
    ) {

        promotions.remove(
                promotion
        );

        promotion.setCampaign(
                null
        );
    }

    public boolean isActive() {

        return status == PromotionStatus.ACTIVE;
    }
}
