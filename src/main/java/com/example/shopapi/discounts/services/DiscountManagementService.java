package com.example.shopapi.discounts.services;

import com.example.shopapi.discounts.entities.AbstractDiscount;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.discounts.enums.DiscountAuditAction;
import com.example.shopapi.discounts.interfaces.DiscountOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountManagementService {


    private final DiscountAuditService auditService;



    public <T extends AbstractDiscount>
    T create(
            T discount,
            DiscountOwner<T> owner,
            User user
    ){

        owner.addDiscount(
                discount
        );


        auditService.log(
                discount,
                user,
                DiscountAuditAction.CREATED,
                "Discount created"
        );


        return discount;
    }



    public <T extends AbstractDiscount>
    T update(
            T discount,
            User user,
            Runnable updater
    ){

        updater.run();

        auditService.log(
                discount,
                user,
                DiscountAuditAction.UPDATED,
                "Discount updated"
        );


        return discount;
    }


    public void delete(
            AbstractDiscount discount,
            User user
    ){

        auditService.log(
                discount,
                user,
                DiscountAuditAction.DELETED,
                "Discount deleted"
        );
    }
}