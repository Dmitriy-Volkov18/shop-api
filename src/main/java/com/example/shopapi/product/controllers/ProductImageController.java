package com.example.shopapi.product.controllers;

import com.example.shopapi.product.ProductImageFacade;
import com.example.shopapi.product.dto.ProductImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageFacade productImageFacade;


    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<ProductImageResponse> getImages(
            @PathVariable Long productId
    ) {
        return productImageFacade.getImages(productId);
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ProductImageResponse uploadImage(
            @PathVariable Long productId,
            @RequestParam("file")
            MultipartFile file,
            @RequestParam(defaultValue = "false")
            boolean primaryImage
    ) {
        return productImageFacade.uploadImage(
                productId,
                file,
                primaryImage
        );
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductImageResponse> uploadImages(
            @PathVariable Long productId,
            @RequestParam("files")
            List<MultipartFile> files
    ){
        return productImageFacade.uploadImages(
                productId,
                files
        );
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ){
        productImageFacade.deleteImage(productId, imageId);
    }
}