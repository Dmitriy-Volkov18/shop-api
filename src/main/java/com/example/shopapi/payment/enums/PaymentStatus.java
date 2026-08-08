package com.example.shopapi.payment.enums;

public enum PaymentStatus {

    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED;

    public boolean canCancel() {
        return this == PENDING;
    }

    public boolean canRefund() {
        return this == SUCCESS;
    }

    public boolean canRetry() {
        return this == FAILED;
    }

    public boolean isFinished() {
        return switch (this) {
            case FAILED,
                 CANCELLED,
                 REFUNDED -> true;
            default -> false;
        };
    }
}