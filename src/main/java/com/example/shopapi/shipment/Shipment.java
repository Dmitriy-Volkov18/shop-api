package com.example.shopapi.shipment;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.common.exception.ShipmentStatusException;
import com.example.shopapi.order.entities.CustomerOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true
    )
    private CustomerOrder order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private String carrier;

    private String trackingNumber;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    public void process() {
        requireStatus(
                ShipmentStatus.PENDING
        );

        status = ShipmentStatus.PROCESSING;
    }

    public void ship(
            String carrier,
            String trackingNumber
    ) {
        requireStatus(
                ShipmentStatus.PROCESSING
        );

        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.status = ShipmentStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }

    public void deliver() {
        requireStatus(
                ShipmentStatus.SHIPPED
        );

        status = ShipmentStatus.DELIVERED;
        deliveredAt = LocalDateTime.now();
    }

    public void cancel() {
        if(!status.canCancel()){
            throw new ShipmentStatusException(
                    "Delivered shipment cannot be cancelled"
            );
        }

        status = ShipmentStatus.CANCELLED;
    }

    private void requireStatus(
            ShipmentStatus expected
    ){
        if(status != expected){
            throw new ShipmentStatusException(
                    "Required "
                            + expected
                            + " but was "
                            + status
            );
        }
    }
}