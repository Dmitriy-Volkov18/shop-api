package com.example.shopapi.order.repositories;

import com.example.shopapi.order.entities.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<CustomerOrderItem, Long> {

}