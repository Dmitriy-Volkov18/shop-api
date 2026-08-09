package com.example.shopapi.promotion;

import com.example.shopapi.promotion.dto.CreatePromotionRequest;
import com.example.shopapi.promotion.dto.PromotionResponse;
import com.example.shopapi.promotion.dto.UpdatePromotionRequest;
import com.example.shopapi.promotion.services.PromotionFacade;
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
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionFacade facade;

    @GetMapping
    public List<PromotionResponse> getPromotions() {
        return facade.getPromotions();
    }

    @GetMapping("/{id}")
    public PromotionResponse getPromotion(
            @PathVariable Long id
    ) {
        return facade.getPromotion(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionResponse create(
            @Valid @RequestBody CreatePromotionRequest request
    ) {
        return facade.create(request);
    }

    @PutMapping("/{id}")
    public PromotionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePromotionRequest request
    ) {
        return facade.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        facade.delete(id);
    }

    @PostMapping("/{id}/publish")
    public PromotionResponse publish(
            @PathVariable Long id
    ) {
        return facade.publish(id);
    }

    @PostMapping("/{id}/pause")
    public PromotionResponse pause(
            @PathVariable Long id
    ) {
        return facade.pause(id);
    }

    @PostMapping("/{id}/activate")
    public PromotionResponse activate(
            @PathVariable Long id
    ) {
        return facade.activate(id);
    }
}
