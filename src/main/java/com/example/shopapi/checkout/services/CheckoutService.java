package com.example.shopapi.checkout.services;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.card.CartService;
import com.example.shopapi.checkout.dto.CheckoutRequest;
import com.example.shopapi.coupon.dto.CouponApplicationRequest;
import com.example.shopapi.coupon.dto.CouponApplicationResult;
import com.example.shopapi.order.mappers.OrderAddressMapper;
import com.example.shopapi.payment.Payment;
import com.example.shopapi.order.factory.CustomerOrderFactory;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.entities.OrderAddressSnapshot;
import com.example.shopapi.user.services.AddressService;
import com.example.shopapi.order.services.CustomerOrderService;
import com.example.shopapi.coupon.services.CouponApplicationService;
import com.example.shopapi.inventory.InventoryReservationService;
import com.example.shopapi.payment.PaymentFactory;
import com.example.shopapi.shipment.Shipment;
import com.example.shopapi.shipment.ShipmentFactory;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.entities.UserAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutService {

    private final CartService cartService;
    private final CustomerOrderFactory customerOrderFactory;
    private final CustomerOrderService customerOrderService;
    private final CurrentUserService currentUserService;
    private final CheckoutValidationService checkoutValidationService;
    private final PaymentFactory paymentFactory;
    private final AddressService addressService;
    private final ShipmentFactory shipmentFactory;
    private final InventoryReservationService inventoryReservationService;
    private final CouponApplicationService couponApplicationService;
    private final OrderAddressMapper orderAddressMapper;

    public CustomerOrder checkout(CheckoutRequest request) {
        User user = currentUser();
        Cart cart = currentCart(user);

        checkoutValidationService.validate(cart);

        CustomerOrder order = customerOrderFactory.create(user, cart);

        applyCoupon(order, request, user);
        attachShippingAddress(order, user);
        reserveInventory(order, cart);
        createPayment(order, request);
        createShipment(order);

        CustomerOrder saved = customerOrderService.save(order);

        cartService.clear(cart);

        return saved;
    }

    private User currentUser() {
        return currentUserService.getCurrentUserEntity();
    }

    private Cart currentCart(
            User user
    ) {
        return cartService.getByUser(user);
    }

    private void applyCoupon(
            CustomerOrder order,
            CheckoutRequest request,
            User user
    ) {
        if(request.couponCode() == null || request.couponCode().isBlank()) {
            return;
        }

        CouponApplicationResult result =
                couponApplicationService.apply(
                        new CouponApplicationRequest(
                                user,
                                request.couponCode(),
                                order.getTotalPrice()
                        )
                );

        order.applyCoupon(
                result.coupon(),
                result.discountAmount()
        );

        order.setCoupon(result.coupon());
    }

    private void attachShippingAddress(
            CustomerOrder order,
            User user
    ) {
        UserAddress address =
                addressService.getPrimaryAddress(
                        user
                );

        OrderAddressSnapshot snapshot =
                orderAddressMapper.toSnapshot(
                        address
                );

        order.setShippingAddress(snapshot);
    }

    private void createPayment(
            CustomerOrder order,
            CheckoutRequest request
    ) {
        Payment payment =
                paymentFactory.create(
                        order,
                        request.paymentMethodOrDefault()
                );

        order.setPayment(payment);
    }

    private void createShipment(
            CustomerOrder order
    ) {
        Shipment shipment = shipmentFactory.create(order);
        order.setShipment(shipment);
    }

    private void reserveInventory(
            CustomerOrder order,
            Cart cart
    ){
        for(CartItem item : cart.getItems()){
            inventoryReservationService.create(
                    order,
                    item.getVariant(),
                    item.getQuantity()
            );
        }
    }
}