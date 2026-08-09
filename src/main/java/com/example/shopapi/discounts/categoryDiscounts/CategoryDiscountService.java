package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.discounts.categoryDiscounts.dto.CreateCategoryDiscountRequest;
import com.example.shopapi.discounts.categoryDiscounts.dto.UpdateCategoryDiscountRequest;
import com.example.shopapi.category.Category;
import com.example.shopapi.discounts.services.DiscountManagementService;
import com.example.shopapi.discounts.services.DiscountValidationService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryDiscountService {

    private final CategoryDiscountRepository repository;
    private final CategoryDiscountMapper mapper;
    private final DiscountManagementService managementService;
    private final DiscountValidationService validationService;

    @Transactional(readOnly = true)
    public List<CategoryDiscount> getDiscounts(
            Category category
    ){
        return repository.findByCategoryId(category.getId());
    }

    @Transactional(readOnly = true)
    public CategoryDiscount getDiscount(
            Category category,
            Long id
    ){
        return repository
                .findByIdAndCategoryId(
                        id,
                        category.getId()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Discount not found"
                        )
                );
    }

    public CategoryDiscount create(
            Category category,
            CreateCategoryDiscountRequest request,
            User user
    ){
        CategoryDiscount discount =
                mapper.toEntity(
                        request
                );

        validationService.validateDiscount(
                request.type(),
                request.discountValue(),
                request.startsAt(),
                request.endsAt()
        );

        managementService.create(
                discount,
                category,
                user
        );

        return repository.save(discount);
    }

    public CategoryDiscount update(
            CategoryDiscount discount,
            UpdateCategoryDiscountRequest request,
            User user
    ){
        validationService.validateDiscount(
                request.type(),
                request.discountValue(),
                null,
                request.startsAt(),
                request.endsAt()
        );

        managementService.update(
                discount,
                user,
                () ->
                        mapper.updateEntity(
                                request,
                                discount
                        )
        );

        return repository.save(discount);
    }

    public void delete(
            CategoryDiscount discount,
            User user
    ){
        managementService.delete(
                discount,
                user
        );

        repository.delete(discount);
    }

}