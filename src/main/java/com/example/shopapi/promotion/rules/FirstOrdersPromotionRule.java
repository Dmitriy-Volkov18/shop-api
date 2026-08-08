package com.example.shopapi.promotion.rules;

import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.order.repositories.CustomerOrderRepository;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.interfaces.PromotionRule;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirstOrdersPromotionRule implements PromotionRule {

    private final CustomerOrderRepository orderRepository;

    @Override
    public PromotionRuleType getType() {
        return PromotionRuleType.FIRST_N_ORDERS;
    }

    @Override
    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        User user = context.getUser();

        if(user == null){
            return false;
        }

        long count =
                orderRepository.countByUserIdAndStatus(
                        user.getId(),
                        CustomerOrderStatus.DELIVERED
                );

        return count < promotion.getRuleValue().longValue();
    }
}