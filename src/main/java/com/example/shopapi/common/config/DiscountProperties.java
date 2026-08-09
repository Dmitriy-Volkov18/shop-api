package com.example.shopapi.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "discount")
@Getter
@Setter
public class DiscountProperties {

    private BigDecimal maxTotalPercent = BigDecimal.valueOf(90);

}