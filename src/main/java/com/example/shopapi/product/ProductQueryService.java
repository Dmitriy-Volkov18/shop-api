package com.example.shopapi.product;

import com.example.shopapi.common.exception.notFoundExceptions.ProductNotFoundException;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductRepository repository;

    public Page<Product> findProducts(
            ProductFilter filter,
            Pageable pageable
    ) {
        Specification<Product> spec = ProductSpecifications.build(filter);
        Pageable sorted = ProductSorting.apply(pageable, filter.getSort());

        return repository.findAll(spec, sorted);
    }

    public Product getById(Long id) {
        return repository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));
    }
}