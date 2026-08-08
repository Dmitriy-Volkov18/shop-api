package com.example.shopapi.brand;

import com.example.shopapi.brand.dto.CreateBrandRequest;
import com.example.shopapi.brand.dto.UpdateBrandRequest;
import com.example.shopapi.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository repository;
    private final BrandMapper mapper;
    private final BrandValidationService validationService;

    @Transactional(readOnly = true)
    public List<Brand> getBrands() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Brand> getActiveBrands() {
        return repository.findAllByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Brand getBrand(
            Long id
    ) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Brand not found"
                        )
                );
    }

    public Brand create(
            CreateBrandRequest request
    ) {
        if(repository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException(
                    "Brand already exists"
            );
        }

        Brand brand =
                mapper.toEntity(
                        request
                );

        validationService.validate(
                brand
        );

        return repository.save(
                brand
        );
    }

    public Brand update(
            Brand brand,
            UpdateBrandRequest request
    ) {
        if(!brand.getName().equalsIgnoreCase(request.name())
                &&
                repository.existsByNameIgnoreCase(
                        request.name()
                )
        ){
            throw new BadRequestException(
                    "Brand already exists"
            );
        }

        mapper.updateEntity(
                request,
                brand
        );

        validationService.validate(
                brand
        );

        return repository.save(
                brand
        );
    }

    public void delete(
            Brand brand
    ) {
        repository.delete(
                brand
        );
    }

    public void activate(
            Brand brand
    ) {
        brand.activate();
    }

    public void deactivate(
            Brand brand
    ) {
        brand.deactivate();
    }
}