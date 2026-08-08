package com.example.shopapi.discounts.interfaces;

import com.example.shopapi.discounts.entities.AbstractDiscount;

public interface DiscountOwner<T extends AbstractDiscount> {

    void addDiscount(
            T discount
    );

}