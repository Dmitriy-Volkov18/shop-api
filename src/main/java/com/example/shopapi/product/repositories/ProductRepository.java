package com.example.shopapi.product.repositories;

import com.example.shopapi.product.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {
    @Query("""
    select distinct p
    from Product p

    left join fetch p.category
    left join fetch p.user

    left join fetch p.images

    left join fetch p.variants v
    left join fetch v.attributes
    left join fetch v.images

    where p.id = :id
""")
    Optional<Product> findByIdWithRelations(
            @Param("id") Long id
    );

    @EntityGraph(attributePaths = {
            "user",
            "category",
            "images"
    })
    Page<Product> findAll(
            Specification<Product> spec,
            Pageable pageable
    );


    @Query("""
    select distinct p
    from Product p

    left join fetch p.category
    left join fetch p.user

    left join fetch p.images

    left join fetch p.variants v
    left join fetch v.attributes
    left join fetch v.images

    where p.id in :ids
""")
    List<Product> findAllByIdsWithRelations(
            @Param("ids") List<Long> ids
    );




}