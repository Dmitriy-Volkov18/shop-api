package com.example.shopapi.product.services;

import com.example.shopapi.brand.BrandService;
import com.example.shopapi.brand.Brand;
import com.example.shopapi.category.Category;
import com.example.shopapi.category.CategoryService;
import com.example.shopapi.common.infrastructure.redis.RedisTrendingService;
import com.example.shopapi.product.ProductFilter;
import com.example.shopapi.product.ProductQueryService;
import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.notFoundExceptions.ProductNotFoundException;
import com.example.shopapi.product.mappers.ProductMapper;
import com.example.shopapi.product.dto.CreateProductRequest;
import com.example.shopapi.product.dto.UpdateProductRequest;
import com.example.shopapi.product.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ImageUploadService imageUploadService;
    private final BrandService brandService;
    private final ProductQueryService productQueryService;
    private final CategoryService categoryService;
    private final RedisTrendingService redisTrendingService;

    public Page<Product> getProducts(
            ProductFilter filter,
            Pageable pageable
    ) {
        return productQueryService.findProducts(filter, pageable);
    }

    @Transactional
    public Product createProduct(
            User user,
            CreateProductRequest request,
            List<MultipartFile> files
    ) {
        Category category = categoryService.getCategory(request.categoryId());
        Brand brand = brandService.getBrand(request.brandId());
        Product product = productMapper.toEntity(request);

        initializeProduct(product, user, category, brand);

        attachImages(
                product,
                files,
                true
        );

        product.recalcStockStatus();

        return productRepository.save(product);
    }

    private void initializeProduct(
            Product product,
            User user,
            Category category,
            Brand brand
    ) {
        product.setUser(user);
        product.setCategory(category);
        product.setBrand(brand);

        product.setReviewCount(0);
        product.setAverageRating(BigDecimal.ZERO);
    }

    public Product getProduct(Long id) {
        return productRepository.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id)
                );
    }

    @Transactional
    public Product updateProduct(
            Product product,
            UpdateProductRequest request,
            List<MultipartFile> files
    ) {
        Category category = categoryService.getCategory(request.categoryId());
        Brand brand = brandService.getBrand(request.brandId());

        productMapper.updateEntity(request, product);

        product.setCategory(category);
        product.setBrand(brand);

        attachImages(
                product,
                files,
                false
        );

        product.recalcStockStatus();

        return productRepository.save(product);
    }

    private void attachImages(
            Product product,
            List<MultipartFile> files,
            boolean setFirstImageAsPrimary
    ) {

        if (files == null || files.isEmpty()) {
            return;
        }

        for (int i = 0; i < files.size(); i++) {
            ImageMetadata metadata = imageUploadService.process(files.get(i));

            productImageService.addImage(
                    product,
                    metadata,
                    setFirstImageAsPrimary && i == 0
            );
        }
    }

    @Transactional
    public void deactivate(
            Product product
    ) {
        product.deactivate();
        redisTrendingService.removeProduct(product.getId());
    }

    @Transactional
    public void increaseStock(
            Product product,
            int quantity
    ) {
        product.increaseStock(quantity);
    }

    @Transactional(readOnly = true)
    public List<Product> findAllByIds(
            List<Long> ids
    ) {
        if (ids.isEmpty()) {
            return List.of();
        }

        List<Product> products = productRepository.findAllByIdsWithRelations(ids);

        Map<Long, Product> map =
                products.stream()
                        .collect(Collectors.toMap(
                                Product::getId,
                                p -> p
                        ));

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
    }
}