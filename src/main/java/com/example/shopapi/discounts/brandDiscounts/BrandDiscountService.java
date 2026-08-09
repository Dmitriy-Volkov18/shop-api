package com.example.shopapi.discounts.brandDiscounts;

import com.example.shopapi.discounts.brandDiscounts.dto.CreateBrandDiscountRequest;
import com.example.shopapi.discounts.brandDiscounts.dto.UpdateBrandDiscountRequest;
import com.example.shopapi.brand.Brand;
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
public class BrandDiscountService {

    private final BrandDiscountRepository repository;
    private final BrandDiscountMapper mapper;
    private final DiscountManagementService managementService;
    private final DiscountValidationService validationService;

    @Transactional(readOnly = true)
    public List<BrandDiscount> getDiscounts(
            Brand brand
    ) {
        return repository.findByBrandId(brand.getId());
    }

    @Transactional(readOnly = true)
    public BrandDiscount getDiscount(
            Brand brand,
            Long id
    ) {
        return repository
                .findByIdAndBrandId(
                        id,
                        brand.getId()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Discount not found"
                        )
                );
    }

    public BrandDiscount create(
            Brand brand,
            CreateBrandDiscountRequest request,
            User user
    ) {
        BrandDiscount discount = mapper.toEntity(request);

        validationService.validateDiscount(
                request.type(),
                request.discountValue(),
                request.startsAt(),
                request.endsAt()
        );

        managementService.create(
                discount,
                brand,
                user
        );

        return repository.save(discount);
    }


    public BrandDiscount update(
            BrandDiscount discount,
            UpdateBrandDiscountRequest request,
            User user
    ) {
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
            BrandDiscount discount,
            User user
    ) {
        managementService.delete(
                discount,
                user
        );

        repository.delete(discount);
    }

}