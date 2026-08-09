package com.example.shopapi.order.controllers;

import com.example.shopapi.checkout.dto.CheckoutRequest;
import com.example.shopapi.order.CustomerOrderFilter;
import com.example.shopapi.order.dto.CustomerOrderResponse;
import com.example.shopapi.order.dto.ReorderResult;
import com.example.shopapi.order.services.CustomerOrderFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderFacade customerOrderFacade;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public CustomerOrderResponse getOrder(@PathVariable Long id) {
        return customerOrderFacade.getOrderById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<CustomerOrderResponse> getOrders(
            CustomerOrderFilter filter,
            Pageable pageable
    ) {
        return customerOrderFacade.getOrders(
                filter,
                pageable
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public Page<CustomerOrderResponse> getMyOrders(
            CustomerOrderFilter filter,
            Pageable pageable
    ){
        return customerOrderFacade.getMyOrders(
                filter,
                pageable
        );
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<CustomerOrderResponse> getCancelledOrders(
            Pageable pageable
    ) {
        return customerOrderFacade.getCancelledOrders(pageable);
    }

    @GetMapping("/me/latest")
    @PreAuthorize("hasRole('USER')")
    public CustomerOrderResponse latest(){
        return customerOrderFacade.getLatestOrder();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrderResponse createOrder(
            @Valid @RequestBody CheckoutRequest request
    ) {
        return customerOrderFacade.createOrder(request);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        customerOrderFacade.cancelOrder(id);
    }

    @PostMapping("/{id}/reorder")
    @PreAuthorize("hasRole('USER')")
    public ReorderResult reorder(
            @PathVariable Long id
    ) {
        return customerOrderFacade.reorder(id);
    }

    @GetMapping("/variants/{variantId}/purchased")
    @PreAuthorize("hasRole('USER')")
    public boolean hasPurchasedVariant(
            @PathVariable Long variantId
    ) {
        return customerOrderFacade.hasPurchasedVariant(variantId);
    }

    @GetMapping("/products/{productId}/purchased")
    @PreAuthorize("hasRole('USER')")
    public boolean hasPurchasedProduct(
            @PathVariable Long productId
    ) {
        return customerOrderFacade.hasPurchasedProduct(productId);
    }
}