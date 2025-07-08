package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, String> {
    @Query(value = "SELECT * FROM auth_code WHERE authorized_code = :authCode and is_deleted = false", nativeQuery = true)
    Optional<AuthCode> findByAuthCode(String authCode);

    Optional<AuthCode> findByAuthorizedCodeAndIsDeletedFalse(String authorizedCode);
}

