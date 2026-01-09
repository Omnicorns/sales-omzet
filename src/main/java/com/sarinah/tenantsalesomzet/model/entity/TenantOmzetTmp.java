package com.sarinah.tenantsalesomzet.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tenant_omzet_tmp")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TenantOmzetTmp {

    @Id
    @Column(name = "tenant_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private String tenantId;

    @Column(name = "sales_date")
    @Temporal(TemporalType.TIMESTAMP)
    @ToString.Include
    private Date salesDate;

    @Column(name = "tenant_name")
    @ToString.Include
    private String tenantName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "day")
    private String day;

    @Column(name = "lot_location")
    private String lotLocation;

    @Column(name="omzet", precision = 15, scale = 2)
    private BigDecimal omzet;

    @Column(name = "created_time", nullable = false)
    private Timestamp createdTime; // Timestamp TIDAK perlu @Temporal

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_time")
    private Timestamp updatedTime;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "updated_by")
    private String updatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name="approval_status", nullable=false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private String submittedBy;
    private Timestamp submittedTime;

    private String approvedBy;
    private Timestamp approvedTime;

    private String rejectedBy;
    private Timestamp rejectedTime;

    @Column(columnDefinition="text")
    private String rejectReason;

    @Version
    private Long version;

    // ✅ PENTING: mappedBy harus sama persis nama field di child = "tenantOmzet"
    @OneToMany(mappedBy = "tenantOmzet", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TenantOmzetReceiptTmp> receipts = new ArrayList<>();

    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }
}
