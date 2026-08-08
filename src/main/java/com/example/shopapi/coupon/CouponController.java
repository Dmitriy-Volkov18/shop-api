package com.example.shopapi.coupon;

import com.example.shopapi.coupon.dto.CouponResponse;
import com.example.shopapi.coupon.dto.CreateCouponRequest;
import com.example.shopapi.coupon.dto.UpdateCouponRequest;
import com.example.shopapi.coupon.services.CouponFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade facade;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CouponResponse> getCoupons() {
        return facade.getCoupons();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponResponse getCoupon(
            @PathVariable Long id
    ) {
        return facade.getCoupon(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CouponResponse create(
            @Valid
            @RequestBody
            CreateCouponRequest request
    ) {
        return facade.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponResponse update(
            @PathVariable Long id,
            @Valid
            @RequestBody
            UpdateCouponRequest request
    ) {
        return facade.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
            @PathVariable Long id
    ) {
        facade.delete(id);
    }
}