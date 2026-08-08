package com.example.shopapi.order.repositories;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>,
        JpaSpecificationExecutor<CustomerOrder> {


    @EntityGraph(attributePaths = {
            "items",
            "items.variant",
            "items.variant.product",
            "payment",
            "shipment",
            "returnRequest"
    })
    Page<CustomerOrder> findAll(
            Specification<CustomerOrder> specification,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "items",
            "items.variant",
            "items.variant.product",
            "payment",
            "shipment",
            "returnRequest"
    })
    Optional<CustomerOrder> findWithDetailsById(Long id);


    List<CustomerOrder> findByUserIdAndStatus(
            Long userId,
            CustomerOrderStatus status
    );

    boolean existsByUserId(
            Long userId
    );

    long countByUserIdAndStatus(
            Long userId,
            CustomerOrderStatus status
    );

    boolean existsByUserIdAndStatusAndItemsVariantId(
            Long userId,
            CustomerOrderStatus status,
            Long variantId
    );

    boolean existsByUserIdAndStatusAndItemsVariantProductId(
            Long userId,
            CustomerOrderStatus status,
            Long productId
    );

    List<CustomerOrder> findByStatusAndPaymentExpiresAtBefore(
            CustomerOrderStatus status,
            LocalDateTime time
    );

    Optional<CustomerOrder>
    findFirstByUserIdOrderByCreatedAtDesc(
            Long userId
    );
}