package com.example.shopapi.searchHistory;

import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.SearchHistoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchHistoryService {

    private static final int MAX_HISTORY_SIZE = 20;

    private final SearchHistoryRepository repository;

    public void save(
            User user,
            String query
    ) {
        query = normalize(query);

        if (query == null) {
            return;
        }

        Optional<SearchHistory> existing =
                repository.findByUserIdAndQueryIgnoreCase(
                        user.getId(),
                        query
                );

        if (existing.isPresent()) {
            SearchHistory history = existing.get();
            history.setSearchedAt(
                    LocalDateTime.now()
            );

            return;
        }

        SearchHistory history = new SearchHistory();
        history.setUser(user);
        history.setQuery(query);
        history.setSearchedAt(LocalDateTime.now());

        repository.save(history);

        trimHistory(user);
    }

    @Transactional(readOnly = true)
    public Page<SearchHistory> getHistory(
            User user,
            Pageable pageable
    ) {
        return repository.findByUserIdOrderBySearchedAtDesc(
                user.getId(),
                pageable
        );
    }

    public void clear(
            User user
    ) {
        repository.deleteByUserId(user.getId());
    }

    private void trimHistory(
            User user
    ) {

        long count =
                repository.countByUserId(
                        user.getId()
                );

        if (count <= MAX_HISTORY_SIZE) {
            return;
        }

        repository
                .findFirstByUserIdOrderBySearchedAtAsc(
                        user.getId()
                )
                .ifPresent(
                        repository::delete
                );
    }

    private String normalize(
            String query
    ) {
        if (query == null) {
            return null;
        }

        query = query.trim();

        if (query.isBlank()) {
            return null;
        }

        return query;
    }

    public void delete(
            User user,
            Long id
    ){
        SearchHistory history =
                repository.findByIdAndUserId(
                                id,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new SearchHistoryNotFoundException(id));

        repository.delete(history);
    }

    @Transactional(readOnly = true)
    public List<SearchHistory> getItems(
            User user
    ) {
        return repository.findByUserIdOrderBySearchedAtDesc(
                user.getId()
        );
    }
}