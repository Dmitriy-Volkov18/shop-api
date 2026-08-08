package com.example.shopapi.brand;

import com.example.shopapi.brand.dto.BrandResponse;
import com.example.shopapi.brand.dto.CreateBrandRequest;
import com.example.shopapi.brand.dto.UpdateBrandRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandService brandService;
    private final BrandMapper mapper;

    @Transactional(readOnly = true)
    public List<BrandResponse> getBrands() {
        return brandService.getBrands()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> getActiveBrands() {
        return brandService.getActiveBrands()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrand(
            Long id
    ) {
        return mapper.toResponse(
                brandService.getBrand(id)
        );
    }

    @Transactional
    public BrandResponse create(
            CreateBrandRequest request
    ) {
        return mapper.toResponse(
                brandService.create(
                        request
                )
        );
    }

    @Transactional
    public BrandResponse update(
            Long id,
            UpdateBrandRequest request
    ) {
        Brand brand =
                brandService.getBrand(
                        id
                );

        return mapper.toResponse(
                brandService.update(
                        brand,
                        request
                )
        );
    }

    @Transactional
    public void delete(
            Long id
    ) {
        Brand brand =
                brandService.getBrand(
                        id
                );

        brandService.delete(
                brand
        );
    }

    @Transactional
    public void activate(
            Long id
    ) {
        Brand brand =
                brandService.getBrand(
                        id
                );

        brandService.activate(
                brand
        );
    }

    @Transactional
    public void deactivate(
            Long id
    ) {
        Brand brand =
                brandService.getBrand(
                        id
                );

        brandService.deactivate(
                brand
        );
    }
}