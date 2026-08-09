package com.example.shopapi.discounts.brandDiscounts;

import com.example.shopapi.brand.BrandService;
import com.example.shopapi.discounts.brandDiscounts.dto.BrandDiscountResponse;
import com.example.shopapi.discounts.brandDiscounts.dto.CreateBrandDiscountRequest;
import com.example.shopapi.discounts.brandDiscounts.dto.UpdateBrandDiscountRequest;
import com.example.shopapi.brand.Brand;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandDiscountFacade {

    private final BrandDiscountService service;
    private final BrandService brandService;
    private final BrandDiscountMapper mapper;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<BrandDiscountResponse> getDiscounts(
            Long brandId
    ) {
        Brand brand = brandService.getBrand(brandId);

        return service.getDiscounts(
                        brand
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandDiscountResponse getDiscount(
            Long brandId,
            Long discountId
    ) {
        Brand brand = brandService.getBrand(brandId);

        return mapper.toResponse(
                service.getDiscount(
                        brand,
                        discountId
                )
        );
    }

    public BrandDiscountResponse create(
            Long brandId,
            CreateBrandDiscountRequest request
    ) {
        Brand brand = brandService.getBrand(brandId);
        User user = currentUserService.getCurrentUserEntity();

        BrandDiscount discount =
                service.create(
                        brand,
                        request,
                        user
                );

        return mapper.toResponse(
                discount
        );
    }

    public BrandDiscountResponse update(
            Long brandId,
            Long discountId,
            UpdateBrandDiscountRequest request
    ) {
        Brand brand = brandService.getBrand(brandId);

        BrandDiscount discount =
                service.getDiscount(
                        brand,
                        discountId
                );

        User user = currentUserService.getCurrentUserEntity();

        return mapper.toResponse(
                service.update(
                        discount,
                        request,
                        user
                )
        );
    }

    public void delete(
            Long brandId,
            Long discountId
    ) {
        Brand brand = brandService.getBrand(brandId);

        BrandDiscount discount =
                service.getDiscount(
                        brand,
                        discountId
                );

        User user = currentUserService.getCurrentUserEntity();
        service.delete(discount, user);
    }
}