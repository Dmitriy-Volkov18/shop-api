package com.example.shopapi.inventory;

public enum InventoryReservationStatus {

    ACTIVE,
    CONFIRMED,
    RELEASED,
    EXPIRED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isFinished() {
        return this != ACTIVE;
    }
}