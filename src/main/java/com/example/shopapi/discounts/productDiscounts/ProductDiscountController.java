package com.example.shopapi.discounts.productDiscounts;

import com.example.shopapi.discounts.dto.CreateDiscountRequest;
import com.example.shopapi.discounts.productDiscounts.dto.ProductDiscountResponse;
import com.example.shopapi.discounts.dto.UpdateDiscountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(
        "/products/{productId}/variants/{variantId}/discounts"
)
@RequiredArgsConstructor
public class ProductDiscountController {
    private final ProductDiscountFacade facade;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<ProductDiscountResponse> getDiscounts(
            @PathVariable Long productId,
            @PathVariable Long variantId
    ) {

        return facade.getDiscounts(
                productId,
                variantId
        );
    }

    @GetMapping("/{discountId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ProductDiscountResponse getDiscount(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @PathVariable Long discountId
    ) {
        return facade.getDiscount(
                productId,
                variantId,
                discountId
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDiscountResponse createDiscount(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid
            @RequestBody
            CreateDiscountRequest request
    ) {
        return facade.createDiscount(
                productId,
                variantId,
                request
        );
    }

    @PutMapping("/{discountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDiscountResponse updateDiscount(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @PathVariable Long discountId,
            @Valid
            @RequestBody
            UpdateDiscountRequest request
    ) {
        return facade.updateDiscount(
                productId,
                variantId,
                discountId,
                request
        );
    }

    @DeleteMapping("/{discountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDiscount(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @PathVariable Long discountId
    ) {
        facade.deleteDiscount(
                productId,
                variantId,
                discountId
        );
    }
}