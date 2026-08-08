package com.example.shopapi.recentlyViewed;

import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.product.entities.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "recently_viewed_products",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "product_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_recently_viewed_user",
                        columnList = "user_id, viewed_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentlyViewedProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private LocalDateTime viewedAt;

}