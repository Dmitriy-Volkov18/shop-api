package com.example.shopapi.promotion.services;

import com.example.shopapi.brand.BrandRepository;
import com.example.shopapi.category.CategoryRepository;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.common.validation.ValidationUtils;
import com.example.shopapi.product.repositories.ProductRepository;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PromotionValidationService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public void validate(
            Promotion promotion
    ) {

        validateName(promotion);

        validateDates(promotion);

        validatePriority(promotion);

        validateRule(promotion);

        validateTarget(promotion);

        validateActionConfig(promotion);
    }

    private void validateName(
            Promotion promotion
    ) {

        ValidationUtils.requireNotBlank(
                promotion.getName(),
                "Promotion name is required"
        );
    }

    private void validateDates(
            Promotion promotion
    ) {

        ValidationUtils.requireDateRange(
                promotion.getStartsAt(),
                promotion.getEndsAt(),
                "Promotion start date must be before end date"
        );
    }

    private void validatePriority(
            Promotion promotion
    ) {

        ValidationUtils.requireNonNegative(
                BigDecimal.valueOf(promotion.getPriority()),
                "Promotion priority cannot be negative"
        );
    }

    private void validateRule(
            Promotion promotion
    ) {


        if(promotion.getRuleType()
                == PromotionRuleType.FIRST_N_ORDERS
                &&
                promotion.getRuleValue() == null) {


            throw new BadRequestException(
                    "Rule value required"
            );
        }


        if(promotion.getRuleValue() != null
                &&
                promotion.getRuleValue()
                        .compareTo(BigDecimal.ZERO) <= 0) {


            throw new BadRequestException(
                    "Rule value must be positive"
            );
        }
    }

    private void validateTarget(
            Promotion promotion
    ) {

        PromotionTargetType type = promotion.getTargetType();
        Long targetId = promotion.getTargetId();

        if (type == PromotionTargetType.ALL) {

            if (targetId != null) {
                throw new BadRequestException(
                        "ALL target must not have targetId"
                );
            }

            return;
        }

        if (type == PromotionTargetType.SAME_PRODUCT) {
            throw new BadRequestException(
                    "SAME_PRODUCT is only allowed for Buy X Get Y configuration"
            );
        }

        if (targetId == null) {
            throw new BadRequestException(
                    "Target id is required"
            );
        }

        boolean exists = switch (type) {

            case PRODUCT ->
                    productRepository.existsById(targetId);

            case CATEGORY ->
                    categoryRepository.existsById(targetId);

            case BRAND ->
                    brandRepository.existsById(targetId);

            case ALL -> true; // сюда не попадём

            case SAME_PRODUCT -> false;
        };

        if (!exists) {
            throw new BadRequestException(
                    switch (type) {

                        case PRODUCT ->
                                "Product target not found";

                        case CATEGORY ->
                                "Category target not found";

                        case BRAND ->
                                "Brand target not found";

                        case ALL ->
                                "Invalid target";

                        case SAME_PRODUCT ->  "Invalid target";
                    }
            );
        }
    }

    private void validateActionConfig(
            Promotion promotion
    ) {

        if(promotion.getActionType()
                == PromotionActionType.BUY_X_GET_Y
                &&
                promotion.getBuyXGetYConfig() == null) {

            throw new BadRequestException(
                    "Buy X Get Y configuration required"
            );
        }


        if(promotion.getActionType()
                != PromotionActionType.BUY_X_GET_Y
                &&
                promotion.getBuyXGetYConfig() != null) {

            throw new BadRequestException(
                    "Buy X Get Y configuration is not allowed"
            );
        }
    }

}