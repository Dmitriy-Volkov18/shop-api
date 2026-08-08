package com.example.shopapi.discounts;

import com.example.shopapi.discounts.entities.DiscountAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountAuditRepository
        extends JpaRepository<DiscountAudit, Long> {


    List<DiscountAudit> findByDiscountIdOrderByCreatedAtDesc(
            Long discountId
    );

}