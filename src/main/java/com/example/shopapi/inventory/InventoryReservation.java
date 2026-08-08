package com.example.shopapi.inventory;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.common.exception.InventoryReservationException;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.productVariant.entities.ProductVariant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_reservations",
        indexes = {

                @Index(
                        name = "idx_reservation_expiration",
                        columnList = "status, expiresAt"
                )

        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            nullable = false
    )
    private ProductVariant variant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private CustomerOrder order;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryReservationStatus status;

    private LocalDateTime expiresAt;

    public void confirm(){
        requireStatus(InventoryReservationStatus.ACTIVE);
        status = InventoryReservationStatus.CONFIRMED;
    }

    public void release(){
        requireStatus(InventoryReservationStatus.ACTIVE);
        status = InventoryReservationStatus.RELEASED;
    }

    public boolean isExpired(){
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void expire(){
        requireStatus(InventoryReservationStatus.ACTIVE);
        status = InventoryReservationStatus.EXPIRED;
    }

    private void requireStatus(
        InventoryReservationStatus expected
    ){
        if(status != expected){
            throw new InventoryReservationException(
                    "Required " + expected + " but was " + status
            );
        }
    }
}