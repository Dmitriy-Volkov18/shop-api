package com.example.shopapi.category;

import com.example.shopapi.category.cache.CategoryCacheService;
import com.example.shopapi.category.dto.CategoryRequest;
import com.example.shopapi.category.dto.CategoryResponse;
import com.example.shopapi.category.dto.CategoryTreeResponse;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.common.exception.ConflictException;
import com.example.shopapi.product.cache.ProductListCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryFacade {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;
    private final CategoryCacheService categoryCacheService;
    private final ProductListCacheService productListCacheService;

    public CategoryResponse create(
            CategoryRequest request
    ) {
        if (categoryService.existsByName(request.name())) {
            throw new ConflictException("Category already exists");
        }

        Category category = mapper.toEntity(request);

        if (request.parentId() != null) {
            Category parent =
                    categoryService.getById(
                            request.parentId()
                    );

            category.setParent(parent);
        }

        Category saved = categoryService.create(category);
        categoryCacheService.evictTree();

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryService.getAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(
            Long id
    ) {
        CategoryResponse cached = categoryCacheService.get(id);

        if (cached != null) {
            return cached;
        }

        CategoryResponse response =
                mapper.toResponse(
                        categoryService.getById(id)
                );

        categoryCacheService.put(response);

        return response;
    }

    public CategoryResponse update(
            Long id,
            CategoryRequest request
    ) {
        Category category = categoryService.getById(id);

        if (categoryService.existsByNameAndIdNot(request.name(), id)) {
            throw new ConflictException("Category already exists");
        }

        Category newParent = null;

        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new BadRequestException(
                        "Category cannot be parent of itself"
                );
            }

            newParent = categoryService.getById(request.parentId());

            validateNoCycle(category, newParent);
        }

        mapper.updateEntity(request, category);

        category.setParent(newParent);

        Category saved = categoryService.save(category);

        categoryCacheService.evictAll(saved.getId());
        productListCacheService.evictAll();

        return mapper.toResponse(saved);
    }

    private void validateNoCycle(
            Category category,
            Category newParent
    ) {
        Category current = newParent;

        while (current != null) {
            if (current.getId().equals(category.getId())) {
                throw new BadRequestException("Category cycle detected");
            }

            current = current.getParent();
        }
    }

    public void delete(Long id) {
        Category category = categoryService.getById(id);

        if (categoryService.hasChildren(id)) {
            throw new BadRequestException(
                    "Cannot delete category with children"
            );
        }

        if (categoryService.hasProducts(id)) {
            throw new BadRequestException(
                    "Cannot delete category with products"
            );
        }

        categoryService.delete(category);
        categoryCacheService.evictAll(category.getId());
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getTree() {
        List<CategoryTreeResponse> cached = categoryCacheService.getTree();

        if (cached != null) {
            return cached;
        }

        List<CategoryTreeResponse> tree =
                categoryService
                        .findRootCategories()
                        .stream()
                        .map(mapper::toTreeResponse)
                        .toList();


        categoryCacheService.putTree(tree);

        return tree;
    }
}