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
@Table(name = "client")
public class Client {
    @Id
    @Column(name = "client_id", nullable = false)
    private String clientId ;

    @Column(name = "auth_client_name", nullable = false)
    private String authClientName;

    @Column(name = "auth_client_id", nullable = false)
    private String authClientId;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_time", nullable = false)
    private Timestamp createdTime;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_time")
    private Timestamp updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;
}

