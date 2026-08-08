package com.example.shopapi.returnProducts;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.common.exception.ReturnStatusException;
import com.example.shopapi.order.entities.CustomerOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private CustomerOrder order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status;

    @Column(nullable = false)
    private String reason;

    private LocalDateTime approvedAt;

    private LocalDateTime completedAt;

    public void approve() {
        changeStatus(ReturnStatus.APPROVED);
        approvedAt = LocalDateTime.now();
    }

    public void reject() {
        changeStatus(ReturnStatus.REJECTED);
    }

    public void complete() {
        changeStatus(ReturnStatus.COMPLETED);
        completedAt = LocalDateTime.now();
    }

    private void changeStatus(
            ReturnStatus target
    ) {
        if (!status.canTransitionTo(target)) {
            throw new ReturnStatusException(
                    "Cannot change return status from "
                            + status
                            + " to "
                            + target
            );
        }

        status = target;
    }
}