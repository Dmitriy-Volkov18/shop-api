package com.example.shopapi.searchHistory;

import java.time.LocalDateTime;

public record SearchHistoryResponse(
        Long id,
        String query,
        LocalDateTime searchedAt
) {
}