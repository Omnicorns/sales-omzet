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
@Table(name = "config")
public class Config {
    @Id
    @Column(name = "config_id")
    private String configId;

    @Column(name = "description")
    private String description;

    @Column(name = "parameter_key")
    private String parameterKey;

    @Column(name = "parameter_value")
    private String parameterValue;


    @Column(name = "is_deleted")
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
