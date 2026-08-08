package com.example.shopapi.productVariant.controllers;

import com.example.shopapi.productVariant.facades.VariantImageFacade;
import com.example.shopapi.productVariant.dto.VariantImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/variants/{variantId}/images")
@RequiredArgsConstructor
public class VariantImageController {

    private final VariantImageFacade facade;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<VariantImageResponse> getImages(
            @PathVariable Long productId,
            @PathVariable Long variantId
    ) {
        return facade.getImages(
                productId,
                variantId
        );
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public VariantImageResponse uploadImage(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestParam("file")
            MultipartFile file,
            @RequestParam(defaultValue = "false")
            boolean primaryImage
    ) {
        return facade.uploadImage(
                productId,
                variantId,
                file,
                primaryImage
        );
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public List<VariantImageResponse> uploadImages(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestParam("files")
            List<MultipartFile> files
    ) {
        return facade.uploadImages(
                productId,
                variantId,
                files
        );
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @PathVariable Long imageId
    ) {
        facade.deleteImage(
                productId,
                variantId,
                imageId
        );
    }
}