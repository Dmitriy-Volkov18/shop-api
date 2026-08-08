package com.example.shopapi.auth.repositories;

import com.example.shopapi.auth.entities.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAuditRepository
        extends JpaRepository<SecurityAuditLog, Long> {
}