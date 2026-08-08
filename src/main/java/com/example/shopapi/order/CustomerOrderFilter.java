package com.example.shopapi.order;

import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.payment.enums.PaymentStatus;
import com.example.shopapi.returnProducts.ReturnStatus;
import com.example.shopapi.shipment.ShipmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CustomerOrderFilter {

    private BigDecimal minTotalPrice;
    private BigDecimal maxTotalPrice;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private List<CustomerOrderStatus> statuses;
    private PaymentStatus paymentStatus;
    private ShipmentStatus shipmentStatus;
    private ReturnStatus returnStatus;
    private Boolean active;
    private Boolean history;

    public void validate(){

        if(Boolean.TRUE.equals(active)
                && Boolean.TRUE.equals(history)){

            throw new IllegalArgumentException(
                    "Cannot use active and history together"
            );
        }
    }

}