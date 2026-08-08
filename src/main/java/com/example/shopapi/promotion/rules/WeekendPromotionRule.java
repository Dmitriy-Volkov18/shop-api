package com.example.shopapi.promotion.rules;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.interfaces.PromotionRule;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class WeekendPromotionRule implements PromotionRule {

    @Override
    public PromotionRuleType getType() {
        return PromotionRuleType.WEEKEND;
    }

    @Override
    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        DayOfWeek day = LocalDate.now().getDayOfWeek();

        return day == DayOfWeek.SATURDAY
                ||
                day == DayOfWeek.SUNDAY;
    }
}
