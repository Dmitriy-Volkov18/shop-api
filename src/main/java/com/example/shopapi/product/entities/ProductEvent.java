package com.example.shopapi.product.entities;

import com.example.shopapi.product.enums.ProductEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_events",
        indexes = {
                @Index(
                        name = "idx_product_event_product",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_product_event_type",
                        columnList = "type"
                ),
                @Index(
                        name = "idx_product_event_created",
                        columnList = "createdAt"
                ),
                @Index(
                        name = "idx_product_event_product_type_created",
                        columnList = "product_id,type,createdAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductEventType type;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}