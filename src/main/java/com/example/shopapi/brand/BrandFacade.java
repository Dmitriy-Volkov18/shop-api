package com.example.shopapi.brand;

import com.example.shopapi.brand.dto.BrandResponse;
import com.example.shopapi.brand.dto.CreateBrandRequest;
import com.example.shopapi.brand.dto.UpdateBrandRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
        return mapper.toResponse(brandService.getBrand(id));
    }

    @Transactional
    public BrandResponse create(
            CreateBrandRequest request
    ) {
        BrandResponse response = mapper.toResponse(brandService.create(request));

        log.info("Brand is created");

        return response;
    }

    @Transactional
    public BrandResponse update(
            Long id,
            UpdateBrandRequest request
    ) {
        Brand brand = brandService.getBrand(id);
        BrandResponse response = mapper.toResponse(brandService.update(brand, request));

        log.info("Brand is updated");

        return response;
    }

    @Transactional
    public void delete(
            Long id
    ) {
        Brand brand = brandService.getBrand(id);
        brandService.delete(brand);

        log.info("Brand is deleted");
    }

    @Transactional
    public void activate(
            Long id
    ) {
        Brand brand = brandService.getBrand(id);
        brandService.activate(brand);

        log.info("Brand is activated");
    }

    @Transactional
    public void deactivate(
            Long id
    ) {
        Brand brand = brandService.getBrand(id);
        brandService.deactivate(brand);

        log.info("Brand is deactivated");
    }
}