package com.example.shopapi.product.cache;

import com.example.shopapi.product.ProductFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class ProductCacheKeyGenerator {


    public String generate(
            ProductFilter filter,
            Pageable pageable
    ) {

        String raw =
                String.join(":",
                        String.valueOf(filter.getSearch()),
                        String.valueOf(filter.getCategoryId()),
                        String.valueOf(filter.getMinPrice()),
                        String.valueOf(filter.getMaxPrice()),
                        String.valueOf(filter.getStatus()),
                        pageable.getPageNumber()+"",
                        pageable.getPageSize()+"",
                        pageable.getSort().toString()
                );


        return DigestUtils
                .md5DigestAsHex(
                        raw.getBytes()
                );
    }
}