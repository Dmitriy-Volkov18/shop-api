package com.example.shopapi.product;

import com.example.shopapi.product.enums.ProductSort;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductSorting {

    public static Pageable apply(
            Pageable pageable,
            ProductSort sort
    ) {

        ProductSort actualSort =
                Objects.requireNonNullElse(
                        sort,
                        ProductSort.NEWEST
                );

        Sort sorting = switch (actualSort) {

            case NEWEST ->
                    Sort.by("createdAt").descending();

            case OLDEST ->
                    Sort.by("createdAt").ascending();

            case PRICE_ASC ->
                    Sort.by("price").ascending();

            case PRICE_DESC ->
                    Sort.by("price").descending();

            case NAME_ASC ->
                    Sort.by("name").ascending();

            case NAME_DESC ->
                    Sort.by("name").descending();

            case RATING_DESC ->
                    Sort.by("averageRating").descending();
        };

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sorting
        );
    }
}