package com.sarinah.tenantsalesomzet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token {
    @Id
    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "scopes")
    private String scopes;

    @Column(name = "auth_client_id", nullable = false)
    private String authClientId;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "access_token_expiry_time")
    private Timestamp accessTokenExpiryTime;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "refresh_token_expiry_time")
    private Timestamp refreshTokenExpiryTime;

    @Column(name = "is_used_token", nullable = false)
    private Boolean isUsedToken;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_time", nullable = false)
    private Timestamp createdTime;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_time")
    private Timestamp updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;
}
