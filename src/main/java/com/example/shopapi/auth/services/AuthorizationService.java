package com.example.shopapi.auth.services;

import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.enums.UserRole;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.reviews.entities.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final CurrentUserService currentUserService;

    public boolean isAdmin() {
        return currentUserService.getCurrentRole() == UserRole.ADMIN;
    }

    public Long currentUserId() {
        return currentUserService.getCurrentUserId();
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Admin privileges required");
        }
    }

    public void requireOwner(Long ownerId) {
        if (!isAdmin() && !currentUserId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public void requireUserAccess(User user) {
        requireOwner(user.getId());
    }

    public void requireProductAccess(Product product) {
        requireOwner(product.getUser().getId());
    }

    public void requireOrderAccess(CustomerOrder order) {
        requireOwner(order.getUser().getId());
    }

    public void requireReviewAccess(Review review) {
        requireOwner(review.getUser().getId());
    }
}