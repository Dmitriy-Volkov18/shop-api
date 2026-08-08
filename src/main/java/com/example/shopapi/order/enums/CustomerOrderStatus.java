package com.example.shopapi.order.enums;

public enum CustomerOrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    RETURN_REQUESTED,
    RETURNED,
    CANCELLED;

    public boolean canPay() {
        return this == PENDING;
    }

    public boolean canShip() {
        return this == PAID;
    }

    public boolean canDeliver() {
        return this == SHIPPED;
    }

    public boolean canCancel() {
        return this == PENDING
                || this == PAID;
    }

    public boolean canReturn() {
        return this == DELIVERED;
    }

    public boolean isFinished() {
        return this == DELIVERED
                || this == RETURNED
                || this == CANCELLED;
    }

    public boolean isActive() {
        return switch (this) {
            case PENDING,
                 PAID,
                 SHIPPED,
                 RETURN_REQUESTED -> true;
            default -> false;
        };
    }

    public boolean isHistory() {
        return !isActive();
    }
}