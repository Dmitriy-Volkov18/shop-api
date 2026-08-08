package com.example.shopapi.promotion.resolvers;

import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.interfaces.PromotionAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionActionResolver {

    private final List<PromotionAction> actions;

    public PromotionAction resolve(
            PromotionActionType type
    ) {
        return actions.stream()
                .filter(action ->
                        action.getType() == type
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No action found for type: "
                                        + type
                        )
                );
    }
}