package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
