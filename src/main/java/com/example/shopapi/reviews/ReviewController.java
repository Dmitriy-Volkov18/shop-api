package com.example.shopapi.reviews;

import com.example.shopapi.reviews.dto.CreateReviewRequest;
import com.example.shopapi.reviews.dto.ReviewResponse;
import com.example.shopapi.reviews.dto.UpdateReviewRequest;
import com.example.shopapi.reviews.services.ReviewFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ReviewController {

    private final ReviewFacadeService reviewFacadeService;

    @PostMapping("/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return reviewFacadeService.create(
                productId,
                request
        );
    }

    @GetMapping("/products/{productId}/reviews")
    public Page<ReviewResponse> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return reviewFacadeService.getProductReviews(
                productId,
                pageable
        );
    }

    @GetMapping("/reviews/{reviewId}")
    public ReviewResponse getById(
            @PathVariable Long reviewId
    ) {
        return reviewFacadeService.getById(reviewId);
    }

    @PutMapping("/reviews/{reviewId}")
    public ReviewResponse update(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        return reviewFacadeService.update(
                reviewId,
                request
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long reviewId
    ) {
        reviewFacadeService.delete(reviewId);
    }

    @GetMapping("/reviews/me")
    public Page<ReviewResponse> getMyReviews(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return reviewFacadeService.getMyReviews(pageable);
    }

    @PostMapping(
            value = "/reviews/{reviewId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadImages(
            @PathVariable Long reviewId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        reviewFacadeService.uploadImages(
                reviewId,
                files
        );
    }

    @DeleteMapping("/reviews/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @PathVariable Long imageId
    ) {
        reviewFacadeService.deleteImage(imageId);
    }

}