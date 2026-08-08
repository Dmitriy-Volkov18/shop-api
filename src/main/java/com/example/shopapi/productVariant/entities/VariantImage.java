package com.example.shopapi.productVariant.entities;

import com.example.shopapi.common.AbstractImage;
import com.example.shopapi.common.interfaces.PrimaryImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "variant_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantImage extends AbstractImage implements PrimaryImage {

    @Column(nullable = false)
    private boolean primaryImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            nullable = false
    )
    private ProductVariant variant;
}