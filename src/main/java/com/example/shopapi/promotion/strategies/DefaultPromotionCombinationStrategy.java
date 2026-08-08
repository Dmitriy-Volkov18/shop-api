package com.example.shopapi.promotion.strategies;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.interfaces.PromotionCombinationStrategy;
import com.example.shopapi.promotion.interfaces.PromotionStackingRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultPromotionCombinationStrategy
        implements PromotionCombinationStrategy {

    private final List<PromotionStackingRule> rules;

    @Override
    public List<Promotion> combine(
            List<Promotion> promotions
    ) {
        List<Promotion> sorted =
                promotions.stream()
                        .sorted(
                                Comparator.comparing(
                                                Promotion::getPriority
                                        )
                                        .reversed()
                        )

                        .toList();

        List<Promotion> selected = new ArrayList<>();

        for(Promotion candidate : sorted){
            boolean allowed =
                    rules.stream()
                            .allMatch(rule ->
                                    rule.isApplicable(
                                            candidate,
                                            selected
                                    )
                            );

            if(!allowed){
                continue;
            }

            selected.add(candidate);

            if(candidate.shouldStopProcessing()) {
                break;
            }
        }

        return selected;
    }
}