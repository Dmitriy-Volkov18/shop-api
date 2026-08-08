package com.example.shopapi.user.repositories;

import com.example.shopapi.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends
        JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Modifying
    @Transactional
    @Query("""
UPDATE User u
SET u.lastActivityAt = :time
WHERE u.id = :userId
""")
    void updateLastActivity(
            @Param("userId") Long userId,
            @Param("time") LocalDateTime time
    );

    Optional<User> findByEmail(String email);


}