package com.example.shopapi.order.repositories;

import com.example.shopapi.order.entities.CustomerOrderItem;
import com.example.shopapi.product.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderItemRepository
        extends JpaRepository<CustomerOrderItem, Long> {

    @Query("""
        select item.variant.product
        from CustomerOrderItem item

        where item.order.id in (

            select oi.order.id
            from CustomerOrderItem oi
            where oi.variant.product.id = :productId

        )

        and item.variant.product.id <> :productId

        and item.variant.product.status =
            com.example.shopapi.product.enums.ProductStatus.ACTIVE

        and exists (

            select v.id
            from ProductVariant v
            where v.product.id = item.variant.product.id

            and (
                v.stockQuantity -
                v.reservedQuantity
            ) > 0

        )

        group by item.variant.product

        order by count(item.id) desc
        """)
    List<Product> findAlsoBought(
            @Param("productId") Long productId,
            Pageable pageable
    );
}