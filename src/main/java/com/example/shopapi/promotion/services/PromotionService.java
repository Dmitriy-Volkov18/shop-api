package com.example.shopapi.promotion.services;

import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.promotion.buyXgetY.BuyXGetYActionConfig;
import com.example.shopapi.promotion.buyXgetY.PromotionActionConfigurationService;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.PromotionMapper;
import com.example.shopapi.promotion.repositories.PromotionRepository;
import com.example.shopapi.promotion.dto.CreatePromotionRequest;
import com.example.shopapi.promotion.dto.UpdatePromotionRequest;
import com.example.shopapi.promotion.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService {

    private final PromotionRepository repository;
    private final PromotionMapper mapper;
    private final PromotionValidationService validationService;
    private final PromotionActionConfigurationService actionConfigurationService;

    @Transactional(readOnly = true)
    public List<Promotion> getPromotions() {

        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Promotion getPromotion(
            Long id
    ) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Promotion not found"
                        )
                );
    }

    public Promotion create(
            CreatePromotionRequest request
    ) {

        if (repository.existsByNameIgnoreCase(
                request.name()
        )) {

            throw new BadRequestException(
                    "Promotion already exists"
            );
        }

        Promotion promotion =
                mapper.toEntity(
                        request
                );

        promotion.setStatus(
                PromotionStatus.DRAFT
        );

        actionConfigurationService.configure(
                promotion,
                request
        );

        validationService.validate(
                promotion
        );

        return repository.save(
                promotion
        );
    }

    public Promotion update(
            Promotion promotion,
            UpdatePromotionRequest request
    ) {

        if (!promotion.getName()
                .equalsIgnoreCase(request.name())
                && repository.existsByNameIgnoreCase(
                request.name()
        )) {

            throw new BadRequestException(
                    "Promotion already exists"
            );
        }

        mapper.updateEntity(
                request,
                promotion
        );

        actionConfigurationService.update(
                promotion,
                request
        );

        validationService.validate(
                promotion
        );

        return repository.save(
                promotion
        );
    }

    public void delete(
            Promotion promotion
    ) {

        repository.delete(
                promotion
        );
    }

    @Transactional
    public void publish(
            Promotion promotion
    ) {

        LocalDateTime now =
                LocalDateTime.now();


        if(promotion.getStatus()
                != PromotionStatus.DRAFT) {

            throw new BadRequestException(
                    "Only draft promotion can be published"
            );
        }


        if(promotion.getStartsAt()
                .isAfter(now)) {

            promotion.setStatus(
                    PromotionStatus.SCHEDULED
            );

        } else {

            promotion.setStatus(
                    PromotionStatus.ACTIVE
            );
        }
    }

    @Transactional
    public void pause(
            Promotion promotion
    ) {

        if(promotion.getStatus()
                != PromotionStatus.ACTIVE) {

            throw new BadRequestException(
                    "Only active promotion can be paused"
            );
        }


        promotion.setStatus(
                PromotionStatus.PAUSED
        );
    }

    @Transactional
    public void activate(
            Promotion promotion
    ) {

        if(promotion.getStatus()
                != PromotionStatus.PAUSED) {

            throw new BadRequestException(
                    "Only paused promotion can be activated"
            );
        }


        promotion.setStatus(
                PromotionStatus.ACTIVE
        );
    }
}