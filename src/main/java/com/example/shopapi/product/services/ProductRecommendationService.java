package com.example.shopapi.product.services;

import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.order.repositories.CustomerOrderItemRepository;
import com.example.shopapi.product.repositories.ProductRepository;
import com.example.shopapi.product.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductRecommendationService {

    private final ProductRepository productRepository;
    private final CustomerOrderItemRepository orderItemRepository;

    public Page<Product> findSimilarProducts(
            Product product,
            Pageable pageable
    ) {
        List<Product> products =
                findBySameBrandAndCategory(
                        product,
                        pageable
                );

        if(products.size() < pageable.getPageSize()) {
            List<Product> fallback =
                    findByCategory(
                            product,
                            pageable
                    );

            products.addAll(
                    fallback.stream()
                            .filter(candidate ->
                                    products.stream()
                                            .noneMatch(existing ->
                                                    existing.getId()
                                                            .equals(candidate.getId())
                                            )
                            )
                            .toList()
            );
        }

        List<Product> result =
                products.stream()
                        .limit(pageable.getPageSize())
                        .toList();

        return new PageImpl<>(
                result,
                pageable,
                products.size()
        );
    }

    private List<Product> findBySameBrandAndCategory(
            Product product,
            Pageable pageable
    ) {
        Specification<Product> specification =
                baseSpecification(product)
                        .and(
                                ProductSpecification.hasBrand(
                                        product.getBrand().getName()
                                )
                        );

        return productRepository.findAll(
                specification,
                sortedPageable(pageable)
        ).getContent();
    }

    private List<Product> findByCategory(
            Product product,
            Pageable pageable
    ) {
        Specification<Product> specification = baseSpecification(product);

        return productRepository.findAll(
                specification,
                sortedPageable(pageable)
        ).getContent();
    }

    private Specification<Product> baseSpecification(
            Product product
    ) {
        return Specification
                .where(
                        ProductSpecification.hasCategory(
                                product.getCategory().getId()
                        )
                )
                .and(
                        ProductSpecification.exclude(
                                product.getId()
                        )
                )
                .and(
                        ProductSpecification.hasStatus(
                                ProductStatus.ACTIVE
                        )
                )
                .and(
                        ProductSpecification.hasAvailableStock(
                                true
                        )
                );
    }

    private Pageable sortedPageable(
            Pageable pageable
    ) {
        Sort sort =
                Sort.by(
                        Sort.Order.desc(
                                "averageRating"
                        ),
                        Sort.Order.desc(
                                "reviewCount"
                        ),
                        Sort.Order.desc(
                                "createdAt"
                        )
                );

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
    }

    public Page<Product> findAlsoBought(
            Product product,
            Pageable pageable
    ) {
        List<Product> products =
                orderItemRepository.findAlsoBought(
                        product.getId(),
                        pageable
                );

        return new PageImpl<>(
                products,
                pageable,
                products.size()
        );
    }
}