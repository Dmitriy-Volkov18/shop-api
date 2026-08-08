package com.example.shopapi.returnProducts;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

public final class ReturnSpecification {

    private ReturnSpecification(){}

    public static Specification<ReturnRequest> hasUserId(Long userId){
        return (root, query, cb) -> {
            if(userId == null){
                return null;
            }

            return cb.equal(
                    root.get("order")
                            .get("user")
                            .get("id"),
                    userId
            );
        };
    }

    public static Specification<ReturnRequest> hasStatus(ReturnStatus status){
        return (root, query, cb) -> {
            if(status == null){
                return null;
            }

            return cb.equal(
                    root.get("status"),
                    status
            );
        };
    }

    private static Specification<ReturnRequest> hasStatuses(
            List<ReturnStatus> statuses
    ) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }

            CriteriaBuilder.In<ReturnStatus> in = cb.in(root.get("status"));

            statuses.forEach(in::value);

            return in;
        };
    }

    public static Specification<ReturnRequest> isActive(
            Boolean active
    ) {
        if (!Boolean.TRUE.equals(active)) {
            return null;
        }

        return hasStatuses(
                Arrays.stream(ReturnStatus.values())
                        .filter(ReturnStatus::isActive)
                        .toList()
        );
    }

    public static Specification<ReturnRequest> isFinished(
            Boolean finished
    ) {
        if (!Boolean.TRUE.equals(finished)) {
            return null;
        }

        return hasStatuses(
                Arrays.stream(ReturnStatus.values())
                        .filter(ReturnStatus::isFinished)
                        .toList()
        );
    }
}