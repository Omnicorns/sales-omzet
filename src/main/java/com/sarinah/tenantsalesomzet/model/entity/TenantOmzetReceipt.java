package com.sarinah.tenantsalesomzet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    private BigDecimal ppn;
    private BigDecimal dpp;
    private BigDecimal serviceCharge;


    @ManyToOne
    @JoinColumn(name = "tenant_omzet_id")
    private TenantOmzet tenantOmzet;
}
