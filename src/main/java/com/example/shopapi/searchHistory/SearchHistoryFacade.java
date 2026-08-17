package com.example.shopapi.searchHistory;

import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchHistoryFacade {

    private final SearchHistoryService searchHistoryService;
    private final SearchHistoryMapper mapper;
    private final CurrentUserService currentUserService;

    public Page<SearchHistoryResponse> getHistory(
            Pageable pageable
    ) {
        User user = currentUser();

        return searchHistoryService
                .getHistory(user, pageable)
                .map(mapper::toResponse);
    }

    public void clearHistory() {
        User user = currentUser();
        searchHistoryService.clear(user);

        log.info("Search history is cleared");
    }

    public void delete(
            Long id
    ){
        User user = currentUser();
        searchHistoryService.delete(user, id);

        log.info("Search history items is deleted");
    }

    private User currentUser() {
        return currentUserService.getCurrentUserEntity();
    }
}