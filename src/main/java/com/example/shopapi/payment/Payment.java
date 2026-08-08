package com.example.shopapi.payment;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.common.exception.PaymentStatusException;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.payment.enums.PaymentMethod;
import com.example.shopapi.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "order_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

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

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(length = 100)
    private String transactionId;

    private LocalDateTime paidAt;

    @Column(length = 500)
    private String failureReason;

    public void markSuccess() {
        requireStatus(PaymentStatus.PENDING);
        paidAt = LocalDateTime.now();
        status = PaymentStatus.SUCCESS;
    }

    public void markFailed(
            String reason
    ) {
        requireStatus(PaymentStatus.PENDING);
        status = PaymentStatus.FAILED;
        failureReason = reason;
    }

    public void cancel() {
        requireStatus(PaymentStatus.PENDING);
        status = PaymentStatus.CANCELLED;
    }

    public void refund() {
        requireStatus(PaymentStatus.SUCCESS);
        status = PaymentStatus.REFUNDED;
    }

    private void requireStatus(
            PaymentStatus expected
    ) {
        if (status != expected) {
            throw new PaymentStatusException(
                    "Expected "
                            + expected
                            + " but was "
                            + status
            );
        }
    }
}