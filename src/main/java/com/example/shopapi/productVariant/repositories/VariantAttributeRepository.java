package com.example.shopapi.productVariant.repositories;

import com.example.shopapi.productVariant.entities.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantAttributeRepository
        extends JpaRepository<VariantAttribute, Long> {

}