package com.example.shopapi.product;

import com.example.shopapi.product.entities.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductSpecifications {

    public static Specification<Product> build(
            ProductFilter filter
    ) {
        return Specification.where(
                        ProductSpecification.hasSearch(
                                filter.getSearch()
                        )
                )
                .and(
                        ProductSpecification.priceGreaterThan(
                                filter.getMinPrice()
                        )
                )
                .and(
                        ProductSpecification.priceLessThan(
                                filter.getMaxPrice()
                        )
                )
                .and(
                        ProductSpecification.hasCategory(
                                filter.getCategoryId()
                        )
                )
                .and(
                        ProductSpecification.hasUser(
                                filter.getUserId()
                        )
                )
                .and(
                        ProductSpecification.hasStatus(
                                filter.getStatus()
                        )
                )
                .and(
                        ProductSpecification.hasBrand(
                                filter.getBrand()
                        )
                )
                .and(
                        ProductSpecification.hasSku(
                                filter.getSku()
                        )
                )
                .and(
                        ProductSpecification.hasMinimumRating(
                                filter.getMinRating()
                        )
                )
                .and(
                        ProductSpecification.hasAvailableStock(
                                filter.getInStock()
                        )
                )
                .and(
                        ProductSpecification.hasDiscount(
                                filter.getDiscounted()
                        )
                )
                .and(
                        ProductSpecification.hasAttributes(
                                filter.getAttributes()
                        )
                );
    }
}