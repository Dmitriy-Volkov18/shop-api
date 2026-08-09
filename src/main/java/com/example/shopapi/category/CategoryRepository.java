package com.example.shopapi.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByParentId(Long parentId);

    boolean existsByIdAndProductsIsNotEmpty(Long categoryId);

    List<Category> findByParentIsNull();
}