package com.example.shopapi.returnProducts.services;

import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.common.exception.ReturnNotFoundException;
import com.example.shopapi.common.exception.ReturnStatusException;
import com.example.shopapi.returnProducts.ReturnFilter;
import com.example.shopapi.returnProducts.ReturnRequest;
import com.example.shopapi.returnProducts.ReturnRequestRepository;
import com.example.shopapi.returnProducts.ReturnSpecification;
import com.example.shopapi.returnProducts.ReturnStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReturnService {
    private final ReturnRequestRepository returnRepository;

    @Transactional(readOnly = true)
    public Page<ReturnRequest> getReturns(
            ReturnFilter filter,
            Pageable pageable,
            Long userId
    ){
        if (Boolean.TRUE.equals(filter.getActive())
                && Boolean.TRUE.equals(filter.getFinished())) {

            throw new BadRequestException(
                    "Only one of active or finished may be specified"
            );
        }

        Specification<ReturnRequest> specification =
                Specification
                        .where(
                                ReturnSpecification.hasUserId(userId)
                        )
                        .and(
                                ReturnSpecification.hasStatus(
                                        filter.getStatus()
                                )
                        )
                        .and(
                                ReturnSpecification.isActive(
                                        filter.getActive()
                                )
                        )
                        .and(
                                ReturnSpecification.isFinished(
                                        filter.getFinished()
                                )
                        );


        return returnRepository.findAll(
                specification,
                pageable
        );
    }

    public ReturnRequest getById(
            Long id
    ){
        return returnRepository.findById(id)
                .orElseThrow(() ->
                        new ReturnNotFoundException(id)
                );
    }

    public ReturnRequest create(
            CustomerOrder order,
            String reason
    ){
        if(!order.canReturn()){
            throw new ReturnStatusException(
                    "Order cannot be returned"
            );
        }

        if(order.getReturnRequest() != null){
            throw new ReturnStatusException(
                    "Return already exists"
            );
        }

        ReturnRequest request = new ReturnRequest();
        request.setOrder(order);
        request.setReason(reason);
        request.setStatus(ReturnStatus.REQUESTED);

        order.requestReturn();
        order.setReturnRequest(request);

        return returnRepository.save(request);
    }

    public void approve(
            ReturnRequest request
    ){
        request.approve();
    }

    public void reject(
            ReturnRequest request
    ){
        request.reject();
    }

    public void complete(
            ReturnRequest request
    ) {
        request.complete();
    }
}