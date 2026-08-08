package com.example.shopapi.promotion.services;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.PromotionMapper;
import com.example.shopapi.promotion.dto.CreatePromotionRequest;
import com.example.shopapi.promotion.dto.PromotionResponse;
import com.example.shopapi.promotion.dto.UpdatePromotionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionFacade {

    private final PromotionService promotionService;
    private final PromotionMapper promotionMapper;

    public List<PromotionResponse> getPromotions() {

        return promotionService.getPromotions()
                .stream()
                .map(promotionMapper::toResponse)
                .toList();
    }

    public PromotionResponse getPromotion(
            Long id
    ) {

        return promotionMapper.toResponse(
                promotionService.getPromotion(id)
        );
    }

    @Transactional
    public PromotionResponse create(
            CreatePromotionRequest request
    ) {

        return promotionMapper.toResponse(
                promotionService.create(request)
        );
    }

    @Transactional
    public PromotionResponse update(
            Long id,
            UpdatePromotionRequest request
    ) {

        Promotion promotion =
                promotionService.getPromotion(id);

        return promotionMapper.toResponse(
                promotionService.update(
                        promotion,
                        request
                )
        );
    }

    @Transactional
    public void delete(
            Long id
    ) {

        promotionService.delete(
                promotionService.getPromotion(id)
        );
    }

    public PromotionResponse publish(
            Long promotionId
    ) {

        Promotion promotion =
                promotionService.getPromotion(
                        promotionId
                );


        promotionService.publish(
                promotion
        );


        return promotionMapper.toResponse(
                promotion
        );
    }



    public PromotionResponse pause(
            Long promotionId
    ) {

        Promotion promotion =
                promotionService.getPromotion(
                        promotionId
                );


        promotionService.pause(
                promotion
        );


        return promotionMapper.toResponse(
                promotion
        );
    }



    public PromotionResponse activate(
            Long promotionId
    ) {

        Promotion promotion =
                promotionService.getPromotion(
                        promotionId
                );


        promotionService.activate(
                promotion
        );


        return promotionMapper.toResponse(
                promotion
        );
    }
}
