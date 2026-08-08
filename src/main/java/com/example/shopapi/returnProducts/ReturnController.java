package com.example.shopapi.returnProducts;

import com.example.shopapi.returnProducts.dto.ReturnRequestCreate;
import com.example.shopapi.returnProducts.dto.ReturnResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {
    private final ReturnFacade facade;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public Page<ReturnResponse> getMyReturns(
            ReturnFilter filter,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ){
        return facade.getMyReturns(
                filter,
                pageable
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReturnResponse> getReturns(
            ReturnFilter filter,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ){
        return facade.getReturns(
                filter,
                pageable
        );
    }

    @PostMapping("/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ReturnResponse create(
            @PathVariable Long orderId,

            @Valid
            @RequestBody ReturnRequestCreate request
    ){
        return facade.create(
                orderId,
                request
        );
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void approve(
            @PathVariable Long id
    ){
        facade.approve(id);
    }

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void reject(
            @PathVariable Long id
    ){
        facade.reject(id);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void complete(
            @PathVariable Long id
    ){
        facade.complete(id);
    }
}