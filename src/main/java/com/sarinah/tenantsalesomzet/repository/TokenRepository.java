package com.sarinah.tenantsalesomzet.repository;


import com.sarinah.tenantsalesomzet.model.entity.Token;
import com.sarinah.tenantsalesomzet.model.projection.PostGetTokenView;
import com.sarinah.tenantsalesomzet.model.projection.TokenClientProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    @Query(value = "SELECT * FROM token WHERE refresh_token = :refreshToken and is_deleted = false", nativeQuery = true)
    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByAccessToken(String refreshToken);

    @Query(value = "SELECT access_token as accessToken, access_token_expiry_time as accessTokenExpiryTime, is_deleted as isDeleted, auth_client_id as authClientId, scopes as scopes, app_id as appId " +
            "FROM token " +
            "WHERE access_token = :accessToken AND is_deleted = false ", nativeQuery = true)
    Optional<PostGetTokenView> getTokenByAccessToken(String accessToken);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update token SET is_used_token  = true, is_deleted = true where app_id  = :appId  and access_token = :accessToken", nativeQuery = true)
    void updateIsUsedTokenAndIsDeleted(String appId, String accessToken);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Token t
        SET t.accessTokenExpiryTime = :newExpiry
        WHERE t.accessToken = :token
    """)
    void updateExpiryTimeByAccessToken(
               String token,
            Timestamp newExpiry
    );

    @Query(
            value = """
        SELECT
          t.access_token             AS accessToken,
          c.auth_client_name             AS authClientName,
          c.auth_client_brand            AS authClientBrand
        FROM token t
        INNER JOIN client c
          ON t.auth_client_id = c.auth_client_id
        WHERE t.access_token = :accessToken
          AND t.is_deleted = FALSE
      """,
            nativeQuery = true
    )
    Optional<TokenClientProjection> findWithClientInfo(
             String accessToken
    );
}
