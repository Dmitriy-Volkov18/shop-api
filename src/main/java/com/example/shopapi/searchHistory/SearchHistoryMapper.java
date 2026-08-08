package com.example.shopapi.searchHistory;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SearchHistoryMapper {

    SearchHistoryResponse toResponse(
            SearchHistory history
    );

    List<SearchHistoryResponse> toResponseList(
            List<SearchHistory> history
    );

}