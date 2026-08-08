package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.discounts.categoryDiscounts.dto.CategoryDiscountResponse;
import com.example.shopapi.discounts.categoryDiscounts.dto.CreateCategoryDiscountRequest;
import com.example.shopapi.discounts.categoryDiscounts.dto.UpdateCategoryDiscountRequest;
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