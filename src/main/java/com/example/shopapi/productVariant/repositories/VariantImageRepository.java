package com.example.shopapi.productVariant.repositories;

import com.example.shopapi.productVariant.entities.VariantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantImageRepository
        extends JpaRepository<VariantImage, Long> {

}