package com.example.shopapi.promotion.buyXgetY;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyXGetYActionConfigRepository
        extends JpaRepository<BuyXGetYActionConfig, Long> {


    Optional<BuyXGetYActionConfig> findByPromotionId(
            Long promotionId
    );

}