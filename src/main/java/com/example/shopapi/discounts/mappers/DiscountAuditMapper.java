package com.example.shopapi.discounts.mappers;

import com.example.shopapi.discounts.dto.DiscountAuditResponse;
import com.example.shopapi.discounts.entities.DiscountAudit;
import org.springframework.stereotype.Component;

@Component
public class DiscountAuditMapper {


    public DiscountAuditResponse toResponse(
            DiscountAudit audit
    ){

        return new DiscountAuditResponse(
                audit.getId(),
                audit.getDiscountId(),
                audit.getAction(),
                audit.getUser().getId(),
                audit.getCreatedAt(),
                audit.getDetails()
        );
    }
}