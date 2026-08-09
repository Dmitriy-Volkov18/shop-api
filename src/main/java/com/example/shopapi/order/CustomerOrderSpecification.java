package com.example.shopapi.order;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.payment.enums.PaymentStatus;
import com.example.shopapi.returnProducts.ReturnStatus;
import com.example.shopapi.shipment.ShipmentStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class CustomerOrderSpecification {

    public static Specification<CustomerOrder> hasUserId(
            Long userId
    ) {
        return (root, query, cb) ->
                userId == null
                        ?
                        null
                        :
                        cb.equal(
                                root.get("user").get("id"),
                                userId
                        );
    }

    public static Specification<CustomerOrder> totalPriceBetween(
            BigDecimal min,
            BigDecimal max
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (min != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("totalPrice"),
                                min
                        )
                );
            }

            if (max != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("totalPrice"),
                                max
                        )
                );
            }

            return predicates.isEmpty()
                    ?
                    null
                    :
                    cb.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
        };
    }

    public static Specification<CustomerOrder> createdBetween(
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                from
                        )
                );
            }

            if (to != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                to
                        )
                );
            }

            return predicates.isEmpty()
                    ?
                    null
                    :
                    cb.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
        };
    }

    public static Specification<CustomerOrder> hasStatuses(
            Collection<CustomerOrderStatus> statuses
    ) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }

            CriteriaBuilder.In<CustomerOrderStatus> in = cb.in(root.get("status"));
            statuses.forEach(in::value);

            return in;
        };
    }

    public static Specification<CustomerOrder> hasPaymentStatus(
            PaymentStatus status
    ) {
        return (root, query, cb) -> {
            if(status == null){
                return null;
            }

            return cb.equal(
                    root.join("payment")
                            .get("status"),
                    status
            );
        };
    }

    public static Specification<CustomerOrder> hasShipmentStatus(
            ShipmentStatus status
    ) {
        return (root, query, cb) -> {
            if(status == null){
                return null;
            }

            return cb.equal(
                    root.join("shipment")
                            .get("status"),
                    status
            );
        };
    }

    public static Specification<CustomerOrder> hasReturnStatus(
            ReturnStatus status
    ) {
        return (root, query, cb) -> {

            if(status == null){
                return null;
            }

            return cb.equal(
                    root.join(
                            "returnRequest",
                            JoinType.LEFT
                            )
                            .get("status"),
                    status
            );
        };
    }

    public static Specification<CustomerOrder> isActive(
            Boolean active
    ) {
        if (!Boolean.TRUE.equals(active)) {
            return null;
        }

        return hasStatuses(
                Arrays.stream(CustomerOrderStatus.values())
                        .filter(CustomerOrderStatus::isActive)
                        .toList()
        );
    }

    public static Specification<CustomerOrder> isHistory(
            Boolean history
    ) {
        if (!Boolean.TRUE.equals(history)) {
            return null;
        }

        return hasStatuses(
                Arrays.stream(CustomerOrderStatus.values())
                        .filter(CustomerOrderStatus::isHistory)
                        .toList()
        );
    }

}