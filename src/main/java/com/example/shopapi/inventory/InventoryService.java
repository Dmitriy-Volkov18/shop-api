package com.example.shopapi.inventory;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.entities.CustomerOrderItem;
import com.example.shopapi.productVariant.services.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final ProductVariantService productVariantService;

    public void restoreFromOrder(
            CustomerOrder order
    ) {
        for(CustomerOrderItem item : order.getItems()) {
            productVariantService.increaseStock(
                    item.getVariant().getId(),
                    item.getQuantity()
            );
        }
    }
}