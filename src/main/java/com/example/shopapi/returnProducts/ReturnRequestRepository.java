package com.example.shopapi.returnProducts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends
        JpaRepository<ReturnRequest, Long>,
        JpaSpecificationExecutor<ReturnRequest> {

    @EntityGraph(attributePaths = {
            "order",
            "order.user"
    })
    Page<ReturnRequest> findAll(
            Specification<ReturnRequest> specification,
            Pageable pageable
    );
}