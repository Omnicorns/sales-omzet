package com.sarinah.tenantsalesomzet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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


    @ManyToOne
    @JoinColumn(name = "tenant_omzet_id")
    private TenantOmzet tenantOmzet;
}
