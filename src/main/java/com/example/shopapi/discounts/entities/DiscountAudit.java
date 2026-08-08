package com.example.shopapi.discounts.entities;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.discounts.enums.DiscountAuditAction;
import com.example.shopapi.user.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "discount_audits",
        indexes = {

                @Index(
                        name = "idx_discount_audit_discount",
                        columnList = "discount_id"
                ),

                @Index(
                        name = "idx_discount_audit_date",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DiscountAudit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false
    )
    private Long discountId;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private DiscountAuditAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(
            columnDefinition = "TEXT"
    )
    private String details;

}