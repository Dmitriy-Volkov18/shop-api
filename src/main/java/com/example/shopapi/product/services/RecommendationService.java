package com.example.shopapi.product.services;

import com.example.shopapi.common.constants.RecommendationWeights;
import com.example.shopapi.order.services.CustomerOrderQueryService;
import com.example.shopapi.product.dto.RecommendationProfile;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.entities.CustomerOrderItem;
import com.example.shopapi.order.services.CustomerOrderService;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.product.repositories.ProductRepository;
import com.example.shopapi.product.ProductSpecification;
import com.example.shopapi.recentlyViewed.RecentlyViewedProduct;
import com.example.shopapi.recentlyViewed.RecentlyViewedService;
import com.example.shopapi.searchHistory.SearchHistory;
import com.example.shopapi.searchHistory.SearchHistoryService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.wishlist.WishlistItem;
import com.example.shopapi.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final WishlistService wishlistService;
    private final RecentlyViewedService recentlyViewedService;
    private final CustomerOrderService customerOrderService;
    private final CustomerOrderQueryService customerOrderQueryService;
    private final ProductRepository productRepository;
    private final SearchHistoryService searchHistoryService;

    public Page<Product> recommend(
            User user,
            Pageable pageable
    ) {
        RecommendationProfile profile = collectProfile(user);

        if (profile.isEmpty()) {
            return popularProducts(pageable);
        }

        Specification<Product> specification =
                Specification
                        .where(
                                ProductSpecification.hasStatus(
                                        ProductStatus.ACTIVE
                                )
                        )
                        .and(
                                ProductSpecification.excludeIds(
                                        profile.excludedProductIds()
                                )
                        )
                        .and(
                                ProductSpecification.matchesRecommendation(
                                        profile
                                )
                        )
                        .and(
                                ProductSpecification.orderByRecommendationScore(
                                        profile
                                )
                        );

        return productRepository.findAll(
                specification,
                pageable
        );
    }

    private Page<Product> popularProducts(
            Pageable pageable
    ) {
        return productRepository.findAll(
                ProductSpecification.active(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(
                                Sort.Order.desc("reviewCount"),
                                Sort.Order.desc("averageRating"),
                                Sort.Order.desc("stockQuantity"),
                                Sort.Order.desc("createdAt")
                        )
                )
        );
    }

    private RecommendationProfile collectProfile(
            User user
    ) {
        Map<Long,Integer> categoryWeights = new HashMap<>();
        Map<Long,Integer> brandWeights = new HashMap<>();
        Map<String,Integer> searchWeights = new HashMap<>();

        Set<Long> excludedIds = new HashSet<>();

        collectWishlist(
                user,
                categoryWeights,
                brandWeights,
                excludedIds
        );

        collectRecentlyViewed(
                user,
                categoryWeights,
                brandWeights,
                excludedIds
        );

        collectOrders(
                user,
                categoryWeights,
                brandWeights,
                excludedIds
        );

        collectSearchHistory(
                user,
                searchWeights
        );

        return new RecommendationProfile(
                categoryWeights,
                brandWeights,
                searchWeights,
                excludedIds
        );
    }

    private void collectWishlist(
            User user,
            Map<Long,Integer> categoryWeights,
            Map<Long,Integer> brandWeights,
            Set<Long> excludedIds
    ) {
        List<WishlistItem> items = wishlistService.getItems(user);

        for(WishlistItem item : items){
            Product product = item.getProduct();

            addProductProfile(
                    product,
                    categoryWeights,
                    brandWeights,
                    RecommendationWeights.WISHLIST
            );

            excludedIds.add(product.getId());
        }
    }

    private void collectRecentlyViewed(
            User user,
            Map<Long,Integer> categoryWeights,
            Map<Long,Integer> brandWeights,
            Set<Long> excludedIds
    ) {
        List<RecentlyViewedProduct> viewed = recentlyViewedService.getItems(user);

        for(RecentlyViewedProduct item : viewed){
            addProductProfile(
                    item.getProduct(),
                    categoryWeights,
                    brandWeights,
                    RecommendationWeights.RECENT_VIEW
            );

            excludedIds.add(item.getProduct().getId());
        }
    }

    private void collectOrders(
            User user,
            Map<Long,Integer> categoryWeights,
            Map<Long,Integer> brandWeights,
            Set<Long> excludedIds
    ){
        List<CustomerOrder> orders = customerOrderQueryService.getDeliveredOrders(user);

        for(CustomerOrder order : orders){
            for(CustomerOrderItem item : order.getItems()){
                addProductProfile(
                        item.getVariant().getProduct(),
                        categoryWeights,
                        brandWeights,
                        RecommendationWeights.ORDER
                );

                excludedIds.add(
                        item.getVariant()
                                .getProduct()
                                .getId()
                );
            }
        }
    }

    private int calculateSearchWeight(
            SearchHistory history
    ){
        long days =
                ChronoUnit.DAYS.between(
                        history.getSearchedAt().toLocalDate(),
                        LocalDate.now()
                );

        if(days <= 1){
            return RecommendationWeights.SEARCH * 2;
        }

        if(days <= 7){
            return RecommendationWeights.SEARCH;
        }

        return 1;
    }

    private void collectSearchHistory(
            User user,
            Map<String,Integer> searchWeights
    ){
        List<SearchHistory> history = searchHistoryService.getItems(user);

        for(SearchHistory item : history){
            searchWeights.merge(
                    item.getQuery().toLowerCase(),
                    calculateSearchWeight(item),
                    Integer::sum
            );
        }
    }

    private void addProductProfile(
            Product product,
            Map<Long,Integer> categoryWeights,
            Map<Long,Integer> brandWeights,
            int weight
    ){
        if(product.getCategory()!=null){
            categoryWeights.merge(
                    product.getCategory().getId(),
                    weight,
                    Integer::sum
            );
        }

        if(product.getBrand()!=null){
            brandWeights.merge(
                    product.getBrand().getId(),
                    weight,
                    Integer::sum
            );
        }
    }

}