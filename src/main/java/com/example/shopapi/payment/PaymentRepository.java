package com.example.shopapi.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    @Query("""
    select p
    from Payment p
    join fetch p.order o
    where o.id = :orderId
    """)
    Optional<Payment> findByOrderIdWithOrder(
            @Param("orderId")
            Long orderId
    );
}