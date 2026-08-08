package com.example.shopapi.productVariant.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class VariantDimensions {
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal length;
}