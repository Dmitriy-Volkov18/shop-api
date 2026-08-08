package com.example.shopapi.auth.services;

import org.springframework.stereotype.Service;

@Service
public class GeoService {

    public String resolveCountry(String ip) {
        // TODO: integrate MaxMind
        return "LT"; // временно
    }
}