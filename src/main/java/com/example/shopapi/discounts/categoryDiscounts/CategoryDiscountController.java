package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.discounts.categoryDiscounts.dto.CategoryDiscountResponse;
import com.example.shopapi.discounts.categoryDiscounts.dto.CreateCategoryDiscountRequest;
import com.example.shopapi.discounts.categoryDiscounts.dto.UpdateCategoryDiscountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories/{categoryId}/discounts")
@RequiredArgsConstructor
public class CategoryDiscountController {

    private final CategoryDiscountFacade facade;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryDiscountResponse> get(
            @PathVariable Long categoryId
    ){
        return facade.getDiscounts(
                categoryId
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDiscountResponse create(
            @PathVariable Long categoryId,
            @Valid
            @RequestBody
            CreateCategoryDiscountRequest request
    ){
        return facade.create(
                categoryId,
                request
        );
    }

    @PutMapping("/{discountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDiscountResponse update(
            @PathVariable Long categoryId,
            @PathVariable Long discountId,
            @Valid
            @RequestBody
            UpdateCategoryDiscountRequest request
    ){
        return facade.update(
                categoryId,
                discountId,
                request
        );
    }

    @DeleteMapping("/{discountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
            @PathVariable Long categoryId,

            @PathVariable Long discountId
    ){
        facade.delete(
                categoryId,
                discountId
        );
    }
}