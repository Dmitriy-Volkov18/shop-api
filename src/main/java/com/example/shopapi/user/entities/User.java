package com.example.shopapi.user.entities;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.common.BaseEntity;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private long tokenVersion = 0;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CustomerOrder> orders = new ArrayList<>();


    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phone;

    @Column
    private String avatarUrl;

    @Column(nullable = false)
    private boolean blocked = false;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private LocalDateTime lastActivityAt;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private Cart cart;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserAddress> addresses =
            new ArrayList<>();


    public void addAddress(UserAddress address){

        addresses.add(address);
        address.setUser(this);
    }


    public void removeAddress(UserAddress address){

        addresses.remove(address);
        address.setUser(null);
    }
}