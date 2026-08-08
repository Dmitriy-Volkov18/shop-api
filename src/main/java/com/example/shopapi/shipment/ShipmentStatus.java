package com.example.shopapi.shipment;

public enum ShipmentStatus {

    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean canProcess() {
        return this == PENDING;
    }

    public boolean canShip() {
        return this == PROCESSING;
    }

    public boolean canDeliver() {
        return this == SHIPPED;
    }

    public boolean canCancel() {
        return this != DELIVERED
                && this != CANCELLED;
    }

    public boolean isFinished() {
        return this == DELIVERED
                || this == CANCELLED;
    }
}