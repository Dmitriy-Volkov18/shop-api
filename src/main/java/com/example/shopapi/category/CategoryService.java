package com.example.shopapi.category;

import com.example.shopapi.common.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public Category getCategory(
            Long id
    ) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(id)
                );
    }

    public Category create(Category category) {
        return repository.save(category);
    }

    public Category getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(id));
    }

    public List<Category> getAll() {
        return repository.findAll();
    }

    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    public boolean existsByNameAndIdNot(
            String name,
            Long id
    ) {
        return repository.existsByNameIgnoreCaseAndIdNot(
                name,
                id
        );
    }

    public Category save(Category category) {
        return repository.save(category);
    }

    public void delete(Category category) {
        repository.delete(category);
    }

    public boolean hasChildren(Long categoryId) {
        return repository.existsByParentId(
                categoryId
        );
    }

    public boolean hasProducts(Long categoryId) {
        return repository.existsByIdAndProductsIsNotEmpty(
                categoryId
        );
    }

    public List<Category> findRootCategories() {
        return repository.findByParentIsNull();
    }

}