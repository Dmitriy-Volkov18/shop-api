package com.example.shopapi.order.services;

import com.example.shopapi.common.exception.notFoundExceptions.CustomerOrderNotFoundException;
import com.example.shopapi.order.CustomerOrderFilter;
import com.example.shopapi.order.CustomerOrderSpecification;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.order.repositories.CustomerOrderRepository;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerOrderQueryService {

    private final CustomerOrderRepository customerOrderRepository;

    @Transactional(readOnly = true)
    public CustomerOrder getOrderEntity(Long id) {

        return customerOrderRepository
                .findWithDetailsById(id)
                .orElseThrow(() ->
                        new CustomerOrderNotFoundException(id)
                );
    }

    @Transactional(readOnly = true)
    public Page<CustomerOrder> getOrders(
            CustomerOrderFilter filter,
            Pageable pageable,
            Long userId
    ) {
        filter.validate();

        Specification<CustomerOrder> specification =
                Specification
                        .where(
                                CustomerOrderSpecification.hasUserId(
                                        userId
                                )
                        )
                        .and(
                                CustomerOrderSpecification.hasStatuses(
                                        filter.getStatuses()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.isActive(
                                        filter.getActive()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.isHistory(
                                        filter.getHistory()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.totalPriceBetween(
                                        filter.getMinTotalPrice(),
                                        filter.getMaxTotalPrice()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.createdBetween(
                                        filter.getFromDate(),
                                        filter.getToDate()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.hasPaymentStatus(
                                        filter.getPaymentStatus()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.hasShipmentStatus(
                                        filter.getShipmentStatus()
                                )
                        )
                        .and(
                                CustomerOrderSpecification.hasReturnStatus(
                                        filter.getReturnStatus()
                                )
                        );

        Pageable sortedPageable = pageable;

        if (pageable.getSort().isUnsorted()) {
            sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdAt").descending()
            );
        }

        return customerOrderRepository.findAll(
                specification,
                sortedPageable
        );
    }

    @Transactional(readOnly = true)
    public CustomerOrder getLatestOrder(
            Long userId
    ){

        return customerOrderRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(
                        () -> new CustomerOrderNotFoundException(userId)
                );
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> getDeliveredOrders(
            User user
    ) {
        return customerOrderRepository.findByUserIdAndStatus(
                user.getId(),
                CustomerOrderStatus.DELIVERED
        );
    }

    public boolean hasPurchasedVariant(
            Long userId,
            Long variantId
    ) {
        return customerOrderRepository
                .existsByUserIdAndStatusAndItemsVariantId(
                        userId,
                        CustomerOrderStatus.DELIVERED,
                        variantId
                );
    }

    public boolean hasPurchasedProduct(
            Long userId,
            Long productId
    ) {
        return customerOrderRepository
                .existsByUserIdAndStatusAndItemsVariantProductId(
                        userId,
                        CustomerOrderStatus.DELIVERED,
                        productId
                );
    }
}
