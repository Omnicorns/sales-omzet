package com.sarinah.tenantsalesomzet.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Login")
public class Login {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "tenantName")
    private String tenantName;

    @Column(name = "tenantBrand")
    private String tenantBrand;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;


    @Column(name = "password")
    private String password;




}
