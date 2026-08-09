package com.example.shopapi.testdata;

import com.example.shopapi.auth.entities.DeviceInfo;
import com.example.shopapi.auth.dto.RegisterRequest;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.shopapi.auth.dto.LoginRequest;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static User validUser(
            PasswordEncoder passwordEncoder
    ) {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@test.com");
        user.setPassword(
                passwordEncoder.encode(
                        "StrongPassword123!"
                )
        );
        user.setRole(UserRole.USER);
        user.setEmailVerified(true);

        return user;
    }

    public static RegisterRequest validRegisterRequest() {
        return new RegisterRequest("john", "john@test.com", "StrongPassword123!");
    }

    public static LoginRequest validLoginRequest() {
        return new LoginRequest("john", "StrongPassword123!");
    }

    public static DeviceInfo validDeviceInfo() {
        return new DeviceInfo(
                "Chrome",
                "150.0",
                "Windows",
                "11",
                "Desktop PC",
                "DESKTOP"
        );
    }

    public static SessionMeta validSessionMeta() {
        return new SessionMeta(
                "127.0.0.1",
                "Lithuania",
                validDeviceInfo(),
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/150.0 Safari/537.36"
        );
    }

}