package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.campaign.dto.CreatePromotionCampaignRequest;
import com.example.shopapi.promotion.campaign.dto.PromotionCampaignResponse;
import com.example.shopapi.promotion.campaign.dto.UpdatePromotionCampaignRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promotion-campaigns")
@RequiredArgsConstructor
public class PromotionCampaignController {

    private final PromotionCampaignFacade facade;


    @GetMapping
    public List<PromotionCampaignResponse> getCampaigns() {
        return facade.getCampaigns();
    }


    @GetMapping("/{id}")
    public PromotionCampaignResponse getCampaign(
            @PathVariable Long id
    ) {
        return facade.getCampaign(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionCampaignResponse create(
            @Valid @RequestBody CreatePromotionCampaignRequest request
    ) {
        return facade.create(request);
    }


    @PutMapping("/{id}")
    public PromotionCampaignResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePromotionCampaignRequest request
    ) {
        return facade.update(id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        facade.delete(id);
    }


    @PostMapping("/{id}/publish")
    public PromotionCampaignResponse publish(
            @PathVariable Long id
    ) {
        return facade.publish(id);
    }


    @PostMapping("/{id}/pause")
    public PromotionCampaignResponse pause(
            @PathVariable Long id
    ) {
        return facade.pause(id);
    }


    @PostMapping("/{id}/activate")
    public PromotionCampaignResponse activate(
            @PathVariable Long id
    ) {
        return facade.activate(id);
    }

    @PostMapping("/{campaignId}/promotions/{promotionId}")
    public PromotionCampaignResponse addPromotion(
            @PathVariable Long campaignId,
            @PathVariable Long promotionId
    ) {

        return facade.addPromotion(
                campaignId,
                promotionId
        );
    }

    @DeleteMapping("/{campaignId}/promotions/{promotionId}")
    public PromotionCampaignResponse removePromotion(
            @PathVariable Long campaignId,
            @PathVariable Long promotionId
    ) {

        return facade.removePromotion(
                campaignId,
                promotionId
        );
    }
}