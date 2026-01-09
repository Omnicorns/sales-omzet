package com.sarinah.tenantsalesomzet.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"tenantOmzet"})  // ✅ Exclude parent reference
@EqualsAndHashCode(exclude = {"tenantOmzet"})  // ✅ Exclude dari equals/hashCode juga
@Entity
@Table(name = "tenant_omzet_receipt")
public class TenantOmzetReceipt {
    @Id
    private String id;

    private String receiptNumber;
    private BigDecimal amount;
    private String paymentType;

    @Column(name = "receipt_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receiptDate;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedTime;

    @Column(name = "updated_by")  // ✅ Tambahkan ini
    private String updatedBy;

    @Column(name = "created_time")  // ✅ Tambahkan ini untuk audit trail
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdTime;

    private BigDecimal ppn;
    private BigDecimal dpp;
    private BigDecimal serviceCharge;

    @ManyToOne(fetch = FetchType.LAZY)  // ✅ Gunakan LAZY untuk performance
    @JoinColumn(name = "tenant_omzet_id")
    private TenantOmzet tenantOmzet;
}