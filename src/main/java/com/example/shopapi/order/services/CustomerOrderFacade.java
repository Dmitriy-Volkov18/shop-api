package com.example.shopapi.order.services;

import com.example.shopapi.card.CartMapper;
import com.example.shopapi.card.CartService;
import com.example.shopapi.card.dto.CartResponse;
import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.checkout.dto.CheckoutRequest;
import com.example.shopapi.order.dto.CustomerOrderResponse;
import com.example.shopapi.order.dto.ReorderResult;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.order.enums.OrderCancellationReason;
import com.example.shopapi.promotion.calculation.CartPriceResult;
import com.example.shopapi.promotion.calculation.CartPricingService;
import com.example.shopapi.promotion.context.PromotionContextBuilder;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.order.CustomerOrderFilter;
import com.example.shopapi.order.mappers.CustomerOrderMapper;
import com.example.shopapi.checkout.services.CheckoutService;
import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerOrderFacade {

    private final CustomerOrderQueryService customerOrderQueryService;
    private final CustomerOrderMapper orderMapper;
    private final AuthorizationService authorizationService;
    private final CurrentUserService currentUserService;
    private final CheckoutService checkoutService;
    private final CustomerOrderMapper customerOrderMapper;
    private final OrderCancellationService orderCancellationService;
    private final ReorderService reorderService;
    private final CartMapper cartMapper;
    private final CartService cartService;
    private final CartPricingService cartPricingService;
    private final PromotionContextBuilder contextBuilder;

    @Transactional
    public ReorderResult reorder(
            Long orderId
    ) {
        User user = currentUserService.getCurrentUserEntity();
        CustomerOrder order = customerOrderQueryService.getOrderEntity(orderId);

        authorizationService.requireOrderAccess(order);

        Cart cart = cartService.getOrCreateCart(user);

        ReorderResult result =
                reorderService.reorder(
                        order,
                        cart
                );

        cartService.save(cart);

        PromotionContext context =
                contextBuilder.build(
                        user,
                        cart,
                        null,
                        null
                );

        CartPriceResult price =
                cartPricingService.calculate(
                        cart,
                        context
                );

        CartResponse cartResponse =
                cartMapper.toResponse(
                        cart,
                        cart.getItems()
                                .stream()
                                .mapToInt(CartItem::getQuantity)
                                .sum(),
                        price.total()
                );

        return new ReorderResult(
                cartResponse,
                result.skippedItems()
        );
    }

    public boolean hasPurchasedVariant(
            Long variantId
    ) {
        User user = currentUserService.getCurrentUserEntity();

        return customerOrderQueryService.hasPurchasedVariant(
                user.getId(),
                variantId
        );
    }

    public boolean hasPurchasedProduct(
            Long productId
    ) {
        User user = currentUserService.getCurrentUserEntity();

        return customerOrderQueryService.hasPurchasedProduct(
                user.getId(),
                productId
        );
    }

    public CustomerOrderResponse createOrder(CheckoutRequest request) {
        CustomerOrder order = checkoutService.checkout(request);

        return customerOrderMapper.toResponse(
                order
        );
    }

    public void cancelOrder(Long id) {
        CustomerOrder order = getAccessibleOrder(id);
        orderCancellationService.cancel(order, OrderCancellationReason.USER_REQUEST);
    }

    public CustomerOrderResponse getOrderById(
            Long id
    ) {
        return orderMapper.toResponse(
                getAccessibleOrder(id)
        );
    }

    public Page<CustomerOrderResponse> getMyOrders(
            CustomerOrderFilter filter,
            Pageable pageable
    ){

        Long userId =
                currentUserService.getCurrentUserId();


        return customerOrderQueryService
                .getOrders(
                        filter,
                        pageable,
                        userId
                )
                .map(
                        orderMapper::toResponse
                );
    }

    public CustomerOrderResponse getLatestOrder(){

        Long userId =
                currentUserService.getCurrentUserId();


        return orderMapper.toResponse(
                customerOrderQueryService
                        .getLatestOrder(userId)
        );
    }

    public Page<CustomerOrderResponse> getOrders(
            CustomerOrderFilter filter,
            Pageable pageable
    ) {

        Long userId =
                authorizationService.isAdmin()
                        ? null
                        : currentUserService.getCurrentUserId();

        Page<CustomerOrder> orders =
                customerOrderQueryService.getOrders(
                        filter,
                        pageable,
                        userId
                );

        return orders.map(orderMapper::toResponse);
    }

    public Page<CustomerOrderResponse> getCancelledOrders(
            Pageable pageable
    ) {

        CustomerOrderFilter filter = new CustomerOrderFilter();
        filter.setStatuses(
                List.of(CustomerOrderStatus.CANCELLED)
        );

        return getOrders(filter, pageable);
    }

    private CustomerOrder getAccessibleOrder(
            Long id
    ) {
        CustomerOrder order = customerOrderQueryService.getOrderEntity(id);
        authorizationService.requireOrderAccess(order);

        return order;
    }

}