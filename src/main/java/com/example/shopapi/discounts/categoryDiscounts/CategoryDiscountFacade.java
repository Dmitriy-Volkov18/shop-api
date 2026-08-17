package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.category.CategoryService;
import com.example.shopapi.discounts.categoryDiscounts.dto.CategoryDiscountResponse;
import com.example.shopapi.discounts.categoryDiscounts.dto.CreateCategoryDiscountRequest;
import com.example.shopapi.discounts.categoryDiscounts.dto.UpdateCategoryDiscountRequest;
import com.example.shopapi.category.Category;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDiscountFacade {

    private final CategoryService categoryService;
    private final CategoryDiscountService discountService;
    private final CategoryDiscountMapper mapper;
    private final CurrentUserService currentUserService;

    public List<CategoryDiscountResponse> getDiscounts(
            Long categoryId
    ){
        Category category = getCategory(categoryId);

        return discountService
                .getDiscounts(category)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CategoryDiscountResponse create(
            Long categoryId,
            CreateCategoryDiscountRequest request
    ){
        Category category = getCategory(categoryId);

        log.info("Category discount is created");

        return mapper.toResponse(
                discountService.create(
                        category,
                        request,
                        currentUserService.getCurrentUserEntity()
                )
        );
    }

    public CategoryDiscountResponse update(
            Long categoryId,
            Long discountId,
            UpdateCategoryDiscountRequest request
    ){
        Category category = getCategory(categoryId);

        CategoryDiscount discount =
                discountService.getDiscount(
                        category,
                        discountId
                );

        log.info("Category discount is updated");

        return mapper.toResponse(
                discountService.update(
                        discount,
                        request,
                        currentUserService.getCurrentUserEntity()
                )
        );
    }

    public void delete(
            Long categoryId,
            Long discountId
    ){
        Category category = getCategory(categoryId);

        CategoryDiscount discount =
                discountService.getDiscount(
                        category,
                        discountId
                );

        discountService.delete(
                discount,
                currentUserService.getCurrentUserEntity()
        );

        log.info("Category discount is deleted");
    }

    private Category getCategory(
            Long id
    ){
        return categoryService.getCategory(id);
    }
}