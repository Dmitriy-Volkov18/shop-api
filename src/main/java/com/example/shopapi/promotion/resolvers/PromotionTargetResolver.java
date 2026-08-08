package com.example.shopapi.promotion.resolvers;

import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.interfaces.PromotionTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionTargetResolver {

    private final List<PromotionTarget> targets;

    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        return targets.stream()
                .filter(target ->
                        target.getType()
                                ==
                                promotion.getTargetType()
                )
                .allMatch(target ->
                        target.matches(
                                promotion,
                                context
                        ));
    }
}