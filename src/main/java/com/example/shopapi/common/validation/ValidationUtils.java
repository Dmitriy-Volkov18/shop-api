package com.example.shopapi.common.validation;

import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requirePositive(
            BigDecimal value,
            String message
    ) {

        if (value == null
                || value.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(message);
        }
    }

    public static void requirePositive(
            Integer value,
            String message
    ) {

        if (value == null || value <= 0) {
            throw new BadRequestException(message);
        }
    }

    public static void requireNonNegative(
            BigDecimal value,
            String message
    ) {

        if (value != null
                && value.compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(message);
        }
    }

    public static void requirePercentage(
            BigDecimal value
    ) {

        requirePositive(
                value,
                "Discount value must be greater than zero"
        );

        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new BadRequestException(
                    "Percentage discount cannot exceed 100%"
            );
        }
    }

    public static void requireDateRange(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            String message
    ) {

        if (startsAt == null || endsAt == null) {

            throw new BadRequestException(
                    "Dates are required"
            );
        }

        if (!startsAt.isBefore(endsAt)) {

            throw new BadRequestException(message);
        }
    }

    public static void requireNotBlank(
            String value,
            String message
    ) {

        if(value == null || value.isBlank()) {

            throw new BadRequestException(
                    message
            );
        }
    }
}