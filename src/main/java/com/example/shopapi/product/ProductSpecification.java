package com.example.shopapi.product;

import com.example.shopapi.brand.Brand;
import com.example.shopapi.discounts.productDiscounts.ProductDiscount;
import com.example.shopapi.product.dto.RecommendationProfile;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.entities.VariantAttribute;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProductSpecification {
    public static Specification<Product> hasSearch(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) {
                return null;
            }

            String pattern = "%" + text.toLowerCase() + "%";

            Join<Product, Brand> brand = root.join("brand");

            return cb.or(
                    cb.like(
                            cb.lower(root.get("name")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(root.get("description")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(brand.get("name")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(root.get("sku")),
                            pattern
                    )
            );
        };
    }

    public static Specification<Product> priceGreaterThan(BigDecimal min) {
        return (root, query, cb) ->
                min == null ? null :
                        cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> priceLessThan(BigDecimal max) {
        return (root, query, cb) ->
                max == null ? null :
                        cb.lessThanOrEqualTo(root.get("price"), max);
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null
                        ? null
                        : cb.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    public static Specification<Product> hasUser(Long userId) {
        return (root, query, cb) ->
                userId == null
                        ? null
                        : cb.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<Product> hasBrand(String brandName) {
        return (root, query, cb) -> {
            if (brandName == null || brandName.isBlank()) {
                return null;
            }

            Join<Product, Brand> brand = root.join("brand");

            return cb.like(
                    cb.lower(brand.get("name")),
                    "%" + brandName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasSku(String sku) {
        return (root, query, cb) ->
                sku == null
                        ? null
                        : cb.equal(root.get("sku"), sku);
    }

    public static Specification<Product> hasMinimumRating(
            BigDecimal rating
    ) {
        return (root, query, cb) ->
                rating == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("averageRating"),
                        rating
                );
    }

    public static Specification<Product> hasAvailableStock(
            Boolean availableOnly
    ) {
        if (availableOnly == null || !availableOnly) {
            return null;
        }

        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);

            Root<ProductVariant> variant = subquery.from(ProductVariant.class);

            subquery.select(variant.get("id"));

            subquery.where(
                    cb.equal(
                            variant.get("product"),
                            root
                    ),
                    cb.greaterThan(
                            cb.diff(
                                    variant.get("stockQuantity"),
                                    variant.get("reservedQuantity")
                            ),
                            0
                    )
            );

            return cb.exists(subquery);
        };
    }

    public static Specification<Product> hasDiscount(
            Boolean discounted
    ) {
        return (root, query, cb) -> {
            if (discounted == null || !discounted) {
                return null;
            }

            Subquery<Long> subquery = query.subquery(Long.class);

            Root<ProductDiscount> discount = subquery.from(ProductDiscount.class);

            Join<ProductDiscount, ProductVariant> variant = discount.join("variant");

            subquery.select(discount.get("id"));

            subquery.where(
                    cb.equal(
                            variant.get("product"),
                            root
                    ),
                    cb.isTrue(
                            discount.get("enabled")
                    ),
                    cb.between(
                            cb.currentTimestamp(),
                            discount.get("startsAt"),
                            discount.get("endsAt")
                    )
            );

            return cb.exists(
                    subquery
            );
        };
    }

    public static Specification<Product> hasAttributes(
            Map<String, List<String>> attributes
    ) {
        return (root, query, cb) -> {
            if (attributes == null || attributes.isEmpty()) {
                return null;
            }

            Subquery<Long> variantSubquery = query.subquery(Long.class);

            Root<ProductVariant> variant = variantSubquery.from(ProductVariant.class);

            variantSubquery.select(variant.get("id"));

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(
                            variant.get("product"),
                            root
                    )
            );

            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {

                Subquery<Long> attributeSubquery =
                        variantSubquery.subquery(Long.class);

                Root<VariantAttribute> attribute =
                        attributeSubquery.from(
                                VariantAttribute.class
                        );

                attributeSubquery.select(
                        attribute.get("id")
                );

                CriteriaBuilder.In<String> values =
                        cb.in(cb.lower(attribute.get("value")));

                for (String value : entry.getValue()) {
                    values.value(value);
                }

                attributeSubquery.where(

                        cb.equal(
                                attribute.get("variant"),
                                variant
                        ),

                        cb.equal(
                                attribute.get("name"),
                                entry.getKey()
                        ),

                        values
                );

                predicates.add(
                        cb.exists(attributeSubquery)
                );
            }

            variantSubquery.where(
                    predicates.toArray(new Predicate[0])
            );

            return cb.exists(
                    variantSubquery
            );
        };
    }

  /*  public static Specification<ProductVariant> hasAvailableStock() {

        return (root, query, cb) ->
                cb.greaterThan(
                        cb.diff(
                                root.get("stockQuantity"),
                                root.get("reservedQuantity")
                        ),
                        0
                );
    }*/

    public static Specification<Product> exclude(
            Long productId
    ) {
        return (root, query, cb) ->
                cb.notEqual(
                        root.get("id"),
                        productId
                );
    }

    public static Specification<Product> hasCategories(
            Collection<Long> categoryIds
    ) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return null;
            }

            return root.get("category")
                    .get("id")
                    .in(categoryIds);
        };
    }

    public static Specification<Product> hasBrands(
            Collection<String> brands
    ) {

        return (root, query, cb) -> {
            if (brands == null || brands.isEmpty()) {
                return null;
            }

            Join<Product, Brand> brand = root.join("brand");

            CriteriaBuilder.In<String> in = cb.in(
                    cb.lower(brand.get("name"))
            );

            brands.stream()
                    .map(String::toLowerCase)
                    .forEach(in::value);

            return in;
        };
    }

    public static Specification<Product> matchesRecommendation(
            RecommendationProfile profile
    ){

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(!profile.categoryWeights().isEmpty()){
                predicates.add(
                        root.get("category")
                                .get("id")
                                .in(
                                        profile.categoryWeights()
                                                .keySet()
                                )
                );
            }

            if(!profile.brandWeights().isEmpty()){
                predicates.add(
                        root.join("brand")
                                .get("id")
                                .in(
                                        profile.brandWeights()
                                                .keySet()
                                )

                );
            }

            return cb.or(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }

    public static Specification<Product> excludeIds(
            Collection<Long> ids
    ) {
        return (root, query, cb) -> {
            if (ids == null || ids.isEmpty()) {
                return cb.conjunction();
            }

            return cb.not(
                    root.get("id").in(ids)
            );
        };
    }

    private static Expression<Integer> categoryScore(
            Root<Product> root,
            CriteriaBuilder cb,
            RecommendationProfile profile
    ) {
        if (profile.categoryWeights().isEmpty()) {
            return cb.literal(0);
        }

        Expression<Integer> score = cb.literal(0);

        for (Map.Entry<Long, Integer> entry : profile.categoryWeights().entrySet()) {

            score = cb.sum(
                    score,
                    cb.<Integer>selectCase()
                            .when(
                                    cb.equal(
                                            root.get("category").get("id"),
                                            entry.getKey()
                                    ),
                                    entry.getValue()
                            )
                            .otherwise(0)
            );
        }

        return score;
    }

    private static Expression<Integer> brandScore(
            Root<Product> root,
            CriteriaBuilder cb,
            RecommendationProfile profile
    ) {
        if (profile.brandWeights().isEmpty()) {
            return cb.literal(0);
        }

        Expression<Integer> score = cb.literal(0);

        Join<Product, Brand> brand = root.join("brand");

        for (Map.Entry<Long, Integer> entry : profile.brandWeights().entrySet()) {

            score = cb.sum(
                    score,
                    cb.<Integer>selectCase()
                            .when(
                                    cb.equal(
                                            brand.get("id"),
                                            entry.getKey()
                                    ),

                                    entry.getValue()
                            )
                            .otherwise(0)
            );
        }

        return score;
    }

    private static Expression<Integer> searchScore(
            Root<Product> root,
            CriteriaBuilder cb,
            RecommendationProfile profile
    ) {
        if (profile.searchWeights().isEmpty()) {
            return cb.literal(0);
        }

        Expression<Integer> score = cb.literal(0);

        Join<Product, Brand> brand = root.join("brand");

        for (Map.Entry<String, Integer> entry : profile.searchWeights().entrySet()) {

            String pattern = "%" + entry.getKey().toLowerCase() + "%";

            Predicate matches =
                    cb.or(
                            cb.like(
                                    cb.lower(root.get("name")),
                                    pattern
                            ),
                            cb.like(
                                    cb.lower(
                                            brand.get("name")
                                    ),
                                    pattern
                            ),
                            cb.like(
                                    cb.lower(root.get("description")),
                                    pattern
                            )
                    );

            score = cb.sum(
                    score,
                    cb.<Integer>selectCase()
                            .when(
                                    matches,
                                    entry.getValue()
                            )
                            .otherwise(0)
            );
        }

        return score;
    }

    public static Expression<Integer> recommendationScore(
            Root<Product> root,
            CriteriaBuilder cb,
            RecommendationProfile profile
    ) {
        Expression<Integer> score =
                cb.literal(0);

        score = cb.sum(
                score,
                categoryScore(
                        root,
                        cb,
                        profile
                )
        );

        score = cb.sum(
                score,
                brandScore(
                        root,
                        cb,
                        profile
                )
        );

        score = cb.sum(
                score,
                searchScore(
                        root,
                        cb,
                        profile
                )
        );

        return score;
    }

    public static Specification<Product> orderByRecommendationScore(
            RecommendationProfile profile
    ){
        return (root, query, cb) -> {
            Expression<Integer> score =
                    recommendationScore(
                            root,
                            cb,
                            profile
                    );

            query.orderBy(
                    cb.desc(score)
            );

            return cb.conjunction();
        };
    }

    public static Specification<Product> active() {
        return hasStatus(ProductStatus.ACTIVE);
    }

}