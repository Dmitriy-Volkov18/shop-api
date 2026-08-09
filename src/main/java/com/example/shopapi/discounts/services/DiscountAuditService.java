package com.example.shopapi.discounts.services;

import com.example.shopapi.discounts.entities.AbstractDiscount;
import com.example.shopapi.discounts.entities.DiscountAudit;
import com.example.shopapi.discounts.DiscountAuditRepository;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.discounts.enums.DiscountAuditAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountAuditService {

    private final DiscountAuditRepository repository;

    public void log(
            AbstractDiscount discount,
            User user,
            DiscountAuditAction action,
            String details
    ){
        DiscountAudit audit = new DiscountAudit();
        audit.setDiscountId(discount.getId());
        audit.setUser(user);
        audit.setAction(action);
        audit.setCreatedAt(LocalDateTime.now());
        audit.setDetails(details);

        repository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<DiscountAudit> getHistory(
            Long discountId
    ){
        return repository
                .findByDiscountIdOrderByCreatedAtDesc(
                        discountId
                );
    }
}