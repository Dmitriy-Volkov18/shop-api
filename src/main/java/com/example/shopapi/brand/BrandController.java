package com.example.shopapi.brand;

import com.example.shopapi.brand.dto.BrandResponse;
import com.example.shopapi.brand.dto.CreateBrandRequest;
import com.example.shopapi.brand.dto.UpdateBrandRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandFacade facade;

    @GetMapping
    public List<BrandResponse> getBrands() {
        return facade.getBrands();
    }

    @GetMapping("/active")
    public List<BrandResponse> getActiveBrands() {
        return facade.getActiveBrands();
    }

    @GetMapping("/{id}")
    public BrandResponse getBrand(
            @PathVariable Long id
    ) {
        return facade.getBrand(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandResponse create(
            @RequestBody CreateBrandRequest request
    ) {
        return facade.create(
                request
        );
    }

    @PutMapping("/{id}")
    public BrandResponse update(
            @PathVariable Long id,
            @RequestBody UpdateBrandRequest request
    ) {
        return facade.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        facade.delete(id);
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(
            @PathVariable Long id
    ) {
        facade.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable Long id
    ) {
        facade.deactivate(id);
    }
}