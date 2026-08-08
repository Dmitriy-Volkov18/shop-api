package com.example.shopapi.promotion.buyXgetY;

import com.example.shopapi.promotion.dto.CreatePromotionRequest;
import com.example.shopapi.promotion.dto.UpdatePromotionRequest;
import com.example.shopapi.promotion.entities.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.shopapi.promotion.enums.PromotionActionType.BUY_X_GET_Y;

@Service
@RequiredArgsConstructor
public class PromotionActionConfigurationService {

    private final BuyXGetYConfigMapper buyXGetYConfigMapper;

    public void configure(
            Promotion promotion,
            CreatePromotionRequest request
    ) {

        switch (request.actionType()) {

            case BUY_X_GET_Y -> {

                BuyXGetYActionConfig config =
                        buyXGetYConfigMapper.toEntity(
                                request.buyXGetYConfig()
                        );

                config.setPromotion(promotion);

                promotion.setBuyXGetYConfig(config);
            }

            default -> {
                // ничего
            }
        }
    }

    public void update(
            Promotion promotion,
            UpdatePromotionRequest request
    ) {

        switch (request.actionType()) {

            case BUY_X_GET_Y -> {

                if (promotion.getBuyXGetYConfig() == null) {

                    BuyXGetYActionConfig config =
                            buyXGetYConfigMapper.toEntity(
                                    request.buyXGetYConfig()
                            );

                    config.setPromotion(
                            promotion
                    );

                    promotion.setBuyXGetYConfig(
                            config
                    );

                } else {

                    buyXGetYConfigMapper.updateEntity(
                            request.buyXGetYConfig(),
                            promotion.getBuyXGetYConfig()
                    );
                }
            }

            default -> promotion.setBuyXGetYConfig(null);
        }
    }
}