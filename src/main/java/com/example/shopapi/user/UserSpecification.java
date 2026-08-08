package com.example.shopapi.user;

import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.enums.UserRole;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) ->
                username == null || username.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                );
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                );
    }

    public static Specification<User> hasRole(UserRole role) {
        return (root, query, cb) ->
                role == null
                        ? null
                        : cb.equal(root.get("role"), role);
    }

}