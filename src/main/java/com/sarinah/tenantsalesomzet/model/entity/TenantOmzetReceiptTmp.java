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
@Entity
@Table(name = "tenant_omzet_receipt_tmp")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TenantOmzetReceiptTmp {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    @ToString.Include
    private String receiptNumber;

    private BigDecimal amount;
    private String paymentType;

    @Column(name = "receipt_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receiptDate;

    @Column(name = "updated_time")
    private Timestamp updatedTime; // Timestamp TIDAK perlu @Temporal

    private BigDecimal ppn;
    private BigDecimal dpp;
    private BigDecimal serviceCharge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_omzet_tmp_id")
    @ToString.Exclude
    private TenantOmzetTmp tenantOmzet;
}
