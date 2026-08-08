package com.example.shopapi.returnProducts;

import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.services.CustomerOrderQueryService;
import com.example.shopapi.returnProducts.dto.ReturnRequestCreate;
import com.example.shopapi.returnProducts.dto.ReturnResponse;
import com.example.shopapi.returnProducts.services.ReturnService;
import com.example.shopapi.returnProducts.services.ReturnWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnFacade {
    private final ReturnService returnService;
    private final CustomerOrderQueryService customerOrderQueryService;
    private final ReturnMapper mapper;
    private final AuthorizationService authorizationService;
    private final ReturnWorkflowService workflowService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<ReturnResponse> getMyReturns(
            ReturnFilter filter,
            Pageable pageable
    ){
        return returnService
                .getReturns(
                        filter,
                        pageable,
                        currentUserService.getCurrentUserId()
                )
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReturnResponse> getReturns(
            ReturnFilter filter,
            Pageable pageable
    ){
        return returnService
                .getReturns(
                        filter,
                        pageable,
                        null
                )
                .map(mapper::toResponse);
    }

    public ReturnResponse create(
            Long orderId,
            ReturnRequestCreate request
    ){
        CustomerOrder order = customerOrderQueryService.getOrderEntity(orderId);

        authorizationService.requireOrderAccess(order);

        ReturnRequest returnRequest =
                returnService.create(
                        order,
                        request.reason()
                );

        return mapper.toResponse(
                returnRequest
        );
    }

    public void approve(Long id){

        ReturnRequest request =
                returnService.getById(id);

        workflowService.approve(request);
    }

    public void reject(Long id){

        ReturnRequest request =
                returnService.getById(id);

        workflowService.reject(request);
    }

    public void complete(Long id){
        ReturnRequest request = returnService.getById(id);
        workflowService.complete(request);
    }
}