package com.sarinah.tenantsalesomzet.repository;


import com.sarinah.tenantsalesomzet.model.entity.Token;
import com.sarinah.tenantsalesomzet.model.projection.PostGetTokenView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    @Query(value = "SELECT * FROM token WHERE refresh_token = :refreshToken and is_deleted = false", nativeQuery = true)
    Optional<Token> findByRefreshToken(String refreshToken);

    @Query(value = "SELECT access_token as accessToken, access_token_expiry_time as accessTokenExpiryTime, is_deleted as isDeleted, auth_client_id as authClientId, scopes as scopes, app_id as appId " +
            "FROM token " +
            "WHERE access_token = :accessToken AND is_deleted = false ", nativeQuery = true)
    Optional<PostGetTokenView> getTokenByAccessToken(String accessToken);
}
