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
@Table(name = "auth_code")
public class AuthCode {
    @Id
    @Column(name = "auth_code_id")
    private String authCodeId;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "scopes")
    private String scopes;

    @Column(name = "authorized_code")
    private String authorizedCode;

    @Column(name = "auth_expiry_time")
    private Timestamp authExpiryTime;

    @Column(name = "is_used_token", nullable = false)
    private Boolean isUsedToken;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted ;

    @Column(name = "created_time", nullable = false)
    private Timestamp createdTime;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_time")
    private Timestamp updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;
}
