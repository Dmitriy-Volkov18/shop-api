package com.example.shopapi.promotion.engine;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionStatus;
import com.example.shopapi.promotion.interfaces.PromotionAction;
import com.example.shopapi.promotion.interfaces.PromotionCombinationStrategy;
import com.example.shopapi.promotion.repositories.PromotionRepository;
import com.example.shopapi.promotion.resolvers.PromotionActionResolver;
import com.example.shopapi.promotion.resolvers.PromotionRuleResolver;
import com.example.shopapi.promotion.resolvers.PromotionTargetResolver;
import com.example.shopapi.promotion.services.PromotionApplicabilityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionEngine {

    private final PromotionRepository repository;
    private final PromotionRuleResolver ruleResolver;
    private final PromotionActionResolver actionResolver;
    private final PromotionCombinationStrategy strategy;
    private final PromotionApplicabilityChecker applicabilityChecker;
    private final PromotionTargetResolver targetResolver;

    public List<AppliedPromotion> apply(
            PromotionContext context
    ) {
        LocalDateTime now =
                context.getNow();

        List<Promotion> promotions =
                repository.findByStatus(
                        PromotionStatus.ACTIVE
                );

        List<Promotion> matched =
                promotions.stream()
                        .filter(promotion ->
                                applicabilityChecker.isApplicable(
                                        promotion,
                                        now
                                )
                        )
                        .filter(promotion ->
                                ruleResolver.matches(
                                        promotion,
                                        context
                                )
                        )
                        .filter(promotion ->
                                targetResolver.matches(
                                        promotion,
                                        context
                                )
                        )
                    .toList();


        List<Promotion> selected =
                strategy.combine(
                        matched
                );

        return selected.stream()
            .map(promotion -> {

                PromotionAction action =
                        actionResolver.resolve(
                                promotion.getActionType()
                        );

                PromotionResult result =
                        action.apply(
                                promotion,
                                context
                        );

                return new AppliedPromotion(
                        promotion,
                        result
                );

            })

            .toList();
    }
}