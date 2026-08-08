package com.example.shopapi.searchHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, Long> {

    Optional<SearchHistory> findByUserIdAndQueryIgnoreCase(
            Long userId,
            String query
    );

    Page<SearchHistory> findByUserIdOrderBySearchedAtDesc(
            Long userId,
            Pageable pageable
    );

    long countByUserId(
            Long userId
    );


    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(
            Long userId
    );

    void deleteByUserId(
            Long userId
    );

    Optional<SearchHistory> findByIdAndUserId(
            Long id,
            Long userId
    );

    Optional<SearchHistory> findFirstByUserIdOrderBySearchedAtAsc(
            Long userId
    );

}