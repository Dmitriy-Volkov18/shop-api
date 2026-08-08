package com.example.shopapi.order.entities;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.coupon.entities.Coupon;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.common.exception.OrderStatusException;
import com.example.shopapi.order.enums.OrderCancellationReason;
import com.example.shopapi.payment.Payment;
import com.example.shopapi.returnProducts.ReturnRequest;
import com.example.shopapi.shipment.Shipment;
import com.example.shopapi.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CustomerOrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerOrderStatus status;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Payment payment;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private OrderAddressSnapshot shippingAddress;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Shipment shipment;

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private ReturnRequest returnRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal subtotal =
            BigDecimal.ZERO;


    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal couponDiscount =
            BigDecimal.ZERO;


    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime paymentExpiresAt;

    @Column(nullable = true)
    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    private OrderCancellationReason cancellationReason;

    private LocalDateTime paidAt;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    public void setReturnRequest(
            ReturnRequest request
    ){
        if (this.returnRequest != null) {
            this.returnRequest.setOrder(null);
        }

        this.returnRequest = request;
        request.setOrder(this);
    }

    public boolean canReturn() {
        return status.canReturn();
    }

    public void requestReturn(){
        requireStatus(CustomerOrderStatus.DELIVERED);
        status = CustomerOrderStatus.RETURN_REQUESTED;
    }

    public void completeReturn(){
        requireStatus(CustomerOrderStatus.RETURN_REQUESTED);
        status = CustomerOrderStatus.RETURNED;
    }

    public void setShipment(
            Shipment shipment
    ){
        if (this.shipment != null) {
            this.shipment.setOrder(null);
        }

        this.shipment = shipment;
        shipment.setOrder(this);
    }

    public void setShippingAddress(
            OrderAddressSnapshot address
    ) {
        this.shippingAddress = address;
        address.setOrder(this);
    }

    public void setPayment(Payment payment) {
        if (this.payment != null) {
            this.payment.setOrder(null);
        }

        this.payment = payment;

        if (payment != null) {
            payment.setOrder(this);
        }
    }

    public void addItem(CustomerOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(CustomerOrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public void calculateTotals() {
        subtotal = calculateSubtotal();
        totalPrice = subtotal.subtract(couponDiscount);

        if(totalPrice.compareTo(BigDecimal.ZERO) < 0){
            totalPrice = BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateSubtotal() {
        return items.stream()
                .map(CustomerOrderItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void applyCoupon(
            Coupon coupon,
            BigDecimal discount
    ) {
        this.coupon = coupon;
        this.couponDiscount = discount;

        calculateTotals();
    }

    public void pay() {
        requireStatus(CustomerOrderStatus.PENDING);
        this.status = CustomerOrderStatus.PAID;
        paidAt = LocalDateTime.now();
    }

    public void ship() {
        requireStatus(CustomerOrderStatus.PAID);
        this.status = CustomerOrderStatus.SHIPPED;
        shippedAt = LocalDateTime.now();
    }

    public void deliver() {
        requireStatus(CustomerOrderStatus.SHIPPED);
        this.status = CustomerOrderStatus.DELIVERED;
        deliveredAt = LocalDateTime.now();
    }

    public boolean canBeCancelled() {
        return status.canCancel();
    }

    public void cancel(OrderCancellationReason reason) {
        if(reason == null){
            throw new IllegalArgumentException(
                    "Cancellation reason is required"
            );
        }

        requireCanBeCancelled();

        status = CustomerOrderStatus.CANCELLED;
        cancelledAt = LocalDateTime.now();
        cancellationReason = reason;
    }

    private void requireCanBeCancelled() {
        if (!canBeCancelled()) {
            throw new OrderStatusException("Order cannot be cancelled in status " + status);
        }
    }

    private void requireStatus(CustomerOrderStatus expected) {
        if (this.status != expected) {
            throw new BadRequestException(
                    "Invalid state: required " + expected + ", but was " + status
            );
        }
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(CustomerOrderItem::getQuantity)
                .sum();
    }

    public void rejectReturn(){

        requireStatus(
                CustomerOrderStatus.RETURN_REQUESTED
        );

        status = CustomerOrderStatus.DELIVERED;
    }
}