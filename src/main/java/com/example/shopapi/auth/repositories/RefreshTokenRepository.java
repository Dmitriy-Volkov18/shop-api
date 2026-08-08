package com.example.shopapi.auth.repositories;

import com.example.shopapi.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findAllByUserId(Long userId);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    Optional<RefreshToken> findByJti(String jti);
    List<RefreshToken> findAllByUserIdOrderByCreatedAtAsc(Long userId);

    List<RefreshToken> findAllByUserIdAndRevokedFalseOrderByCreatedAtDesc(Long userId);
    List<RefreshToken> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.familyId = :familyId")
    void revokeAllByFamilyId(@Param("familyId") String familyId);

    @Modifying
    @Transactional
    @Query("""
    update RefreshToken t
       set t.revoked = true
     where t.id = :id
       and t.revoked = false
""")
    int consume(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("""
    delete from RefreshToken r
    where r.expiryDate < :now
""")
    void deleteExpired(@Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("""
        update RefreshToken r
        set r.revoked = true
        where r.jti = :jti
    """)
    void revokeByJti(@Param("jti") String jti);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update RefreshToken r
        set r.revoked = true
        where r.user.id = :userId
    """)
    void revokeAllByUserId(@Param("userId") Long userId);

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Transactional
    @Query("""
    update RefreshToken r
       set r.revoked = true
     where r.user.id = :userId
       and r.jti <> :currentJti
""")
    void revokeAllByUserIdExceptJti(
            @Param("userId") Long userId,
            @Param("currentJti") String currentJti
    );

    List<RefreshToken> findAllByUserIdAndRevokedFalseOrderByCreatedAtAsc(
            Long userId
    );


    @Modifying
    @Transactional
    @Query("""
    delete from RefreshToken r
    where r.user.id = :userId
""")
    void deleteAllByUserId(
            @Param("userId") Long userId
    );
}