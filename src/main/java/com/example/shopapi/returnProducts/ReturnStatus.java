package com.example.shopapi.returnProducts;

/*
public enum ReturnStatus {

    REQUESTED(
            true,
            false,
            true,
            true,
            false
    ),

    APPROVED(
            true,
            false,
            false,
            false,
            true
    ),

    REJECTED(
            false,
            true,
            false,
            false,
            false
    ),

    COMPLETED(
            false,
            true,
            false,
            false,
            false
    ),

    CANCELLED(
            false,
            true,
            false,
            false,
            false
    );

    private final boolean active;
    private final boolean finished;

    private final boolean canApprove;
    private final boolean canReject;
    private final boolean canComplete;

    ReturnStatus(
            boolean active,
            boolean finished,
            boolean canApprove,
            boolean canReject,
            boolean canComplete
    ) {
        this.active = active;
        this.finished = finished;
        this.canApprove = canApprove;
        this.canReject = canReject;
        this.canComplete = canComplete;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean canApprove() {
        return canApprove;
    }

    public boolean canReject() {
        return canReject;
    }

    public boolean canComplete() {
        return canComplete;
    }
}*/

public enum ReturnStatus {

    REQUESTED,
    APPROVED,
    REJECTED,
    COMPLETED,
    CANCELLED;


    public boolean canTransitionTo(
            ReturnStatus target
    ) {

        return switch (this) {

            case REQUESTED ->
                    target == APPROVED
                            || target == REJECTED;

            case APPROVED ->
                    target == COMPLETED
                            || target == CANCELLED;

            case REJECTED,
                 COMPLETED,
                 CANCELLED ->
                    false;
        };
    }


    public boolean isActive() {
        return switch (this) {
            case REQUESTED,
                 APPROVED -> true;

            default -> false;
        };
    }


    public boolean isFinished() {
        return switch (this) {
            case REJECTED,
                 COMPLETED,
                 CANCELLED -> true;

            default -> false;
        };
    }
}