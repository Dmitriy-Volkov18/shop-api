package com.example.shopapi.searchHistory;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "search_history",
        indexes = {

                @Index(
                        name = "idx_search_history_user_date",
                        columnList = "user_id, searchedAt"
                )

        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            nullable = false,
            length = 255
    )
    private String query;

    @Column(nullable = false)
    private LocalDateTime searchedAt;
}