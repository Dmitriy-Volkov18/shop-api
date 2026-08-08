package com.example.shopapi.discounts.brandDiscounts;

import com.example.shopapi.discounts.brandDiscounts.dto.BrandDiscountResponse;
import com.example.shopapi.discounts.brandDiscounts.dto.CreateBrandDiscountRequest;
import com.example.shopapi.discounts.brandDiscounts.dto.UpdateBrandDiscountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands/{brandId}/discounts")
@RequiredArgsConstructor
public class BrandDiscountController {

    private final BrandDiscountFacade facade;

    @GetMapping
    public List<BrandDiscountResponse> getDiscounts(
            @PathVariable Long brandId
    ){
        return facade.getDiscounts(
                brandId
        );
    }

    @GetMapping("/{discountId}")
    public BrandDiscountResponse getDiscount(
            @PathVariable Long brandId,
            @PathVariable Long discountId
    ){
        return facade.getDiscount(
                brandId,
                discountId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandDiscountResponse create(
            @PathVariable Long brandId,
            @Valid @RequestBody CreateBrandDiscountRequest request
    ){
        return facade.create(
                brandId,
                request
        );
    }

    @PutMapping("/{discountId}")
    public BrandDiscountResponse update(
            @PathVariable Long brandId,
            @PathVariable Long discountId,
            @Valid @RequestBody UpdateBrandDiscountRequest request
    ){
        return facade.update(
                brandId,
                discountId,
                request
        );
    }

    @DeleteMapping("/{discountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long brandId,
            @PathVariable Long discountId
    ){
        facade.delete(
                brandId,
                discountId
        );
    }
}