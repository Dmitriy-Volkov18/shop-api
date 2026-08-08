package com.example.shopapi.searchHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryFacade facade;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<SearchHistoryResponse> getHistory(
            Pageable pageable
    ) {
        return facade.getHistory(
                pageable
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER')")
    public void clear() {
        facade.clearHistory();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER')")
    public void delete(
            @PathVariable Long id
    ){
        facade.delete(id);
    }
}