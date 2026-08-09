package com.example.shopapi.coupon.services;

import com.example.shopapi.coupon.dto.CreateCouponRequest;
import com.example.shopapi.coupon.dto.UpdateCouponRequest;
import com.example.shopapi.coupon.entities.Coupon;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.coupon.CouponMapper;
import com.example.shopapi.coupon.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository repository;
    private final CouponMapper mapper;
    private final CouponValidationService validationService;

    @Transactional(readOnly = true)
    public List<Coupon> getCoupons() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Coupon getCoupon(
            Long id
    ) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Coupon not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public Coupon getByCode(
            String code
    ) {
        return repository.findByCodeIgnoreCase(code)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Coupon not found"
                        )
                );
    }

    public Coupon create(
            CreateCouponRequest request
    ) {
        Coupon coupon = mapper.toEntity(request);
        validationService.validateForCreate(coupon);

        return repository.save(coupon);
    }

    public Coupon update(
            Coupon coupon,
            UpdateCouponRequest request
    ) {
        String previousCode = coupon.getCode();

        mapper.updateEntity(request, coupon);

        validationService.validateForUpdate(
                coupon,
                previousCode
        );

        return repository.save(coupon);
    }

    public void delete(
            Coupon coupon
    ) {
        repository.delete(coupon);
    }
}