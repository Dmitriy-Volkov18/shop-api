package com.example.shopapi.product.controllers;

import com.example.shopapi.product.ProductDiscoveryFacade;
import com.example.shopapi.product.ProductFacade;
import com.example.shopapi.product.ProductFilter;
import com.example.shopapi.product.dto.CreateProductRequest;
import com.example.shopapi.product.dto.UpdateProductRequest;
import com.example.shopapi.product.enums.ProductSort;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.product.dto.ProductDetailResponse;
import com.example.shopapi.product.dto.ProductListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;
    private final ProductDiscoveryFacade productDiscoveryFacade;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public Page<ProductListResponse> getProducts(
            @RequestParam(required =false) String name,
            @RequestParam(required =false) BigDecimal minPrice,
            @RequestParam(required =false) BigDecimal maxPrice,
            @RequestParam(required =false) Long categoryId,
            @RequestParam(required =false) Long userId,
            @RequestParam(required =false) ProductStatus status,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false)
            ProductSort sort,
            @RequestParam(required = false)
            BigDecimal minRating,

            @RequestParam(required = false)
            Boolean inStock,
            @RequestParam(required = false)
            Boolean discounted,
            @RequestParam(required = false)
            String attributes,
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {

        ProductFilter filter = new ProductFilter();

        filter.setSearch(name);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setCategoryId(categoryId);
        filter.setUserId(userId);
        filter.setStatus(status);
        filter.setBrand(brand);
        filter.setSku(sku);
        filter.setSort(sort);
        filter.setMinRating(minRating);
        filter.setInStock(inStock);
        filter.setDiscounted(discounted);
        filter.setAttributes(
                parseAttributes(attributes)
        );

        return productFacade.getProducts(filter, pageable);
    }

    private Map<String, List<String>> parseAttributes(
            String attributes
    ) {

        Map<String, List<String>> result =
                new HashMap<>();

        if (attributes == null || attributes.isBlank()) {
            return result;
        }

        for (String pair : attributes.split(",")) {

            String[] split = pair.split(":");

            if (split.length != 2) {
                continue;
            }

            String key = split[0]
                    .trim()
                    .toLowerCase();

            List<String> values =
                    Arrays.stream(split[1].split("\\|"))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .filter(s -> !s.isBlank())
                            .toList();

            result.put(key, values);
        }

        return result;
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ProductDetailResponse getById(@PathVariable Long id) {
        return productFacade.getProduct(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProductDetailResponse create(

            @RequestPart("product")
            @Valid
            CreateProductRequest request,


            @RequestPart("images")
            List<MultipartFile> images

    ) {
        return productFacade.createProduct(
                request,
                images
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProductDetailResponse update(

            @PathVariable Long id,


            @RequestPart("product")
            @Valid
            UpdateProductRequest request,


            @RequestPart(value = "images", required = false)
            List<MultipartFile> images

    ) {

        return productFacade.updateProduct(
                id,
                request,
                images
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        productFacade.deactivate(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock")
    public ProductDetailResponse updateStock(
            @PathVariable Long id,
            @RequestParam int quantity
    ) {
        return productFacade.updateStock(id, quantity);
    }

    @GetMapping("/{id}/similar")
    public Page<ProductListResponse> getSimilarProducts(
            @PathVariable Long id,
            Pageable pageable
    ) {

        return productDiscoveryFacade.getSimilarProducts(
                id,
                pageable
        );
    }

    @GetMapping("/{id}/also-bought")
    public Page<ProductListResponse> getAlsoBought(
            @PathVariable Long id,
            Pageable pageable
    ) {

        return productDiscoveryFacade.getAlsoBought(
                id,
                pageable
        );
    }

    @GetMapping("/recommended")
    @PreAuthorize("isAuthenticated()")
    public Page<ProductListResponse> recommend(
            Pageable pageable
    ){
        return productDiscoveryFacade.recommend(pageable);
    }

    @GetMapping("/trending")
    public Page<ProductListResponse> getTrending(
            Pageable pageable
    ) {
        return productDiscoveryFacade.getTrending(
                pageable
        );
    }
}