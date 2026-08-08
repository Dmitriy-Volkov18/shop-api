package com.example.shopapi.user;

import com.example.shopapi.user.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilter {
    private String username;
    private String email;
    private UserRole role;
}