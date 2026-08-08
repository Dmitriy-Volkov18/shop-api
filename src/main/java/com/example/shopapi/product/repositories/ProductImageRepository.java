package com.example.shopapi.product.repositories;

import com.example.shopapi.product.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {

}