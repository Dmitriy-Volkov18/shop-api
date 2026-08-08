package com.example.shopapi.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

        @Size(
                min = 2,
                max = 50,
                message = "First name must be between 2 and 50 characters"
        )
        String firstName,


        @Size(
                min = 2,
                max = 50,
                message = "Last name must be between 2 and 50 characters"
        )
        String lastName,


        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Invalid phone number"
        )
        String phone,


        @Size(
                max = 500,
                message = "Avatar URL too long"
        )
        String avatarUrl

) {
}