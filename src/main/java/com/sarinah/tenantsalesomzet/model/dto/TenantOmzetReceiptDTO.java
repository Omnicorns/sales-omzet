package com.sarinah.tenantsalesomzet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantOmzetReceiptDTO {
    private String receiptNumber;
    private Date receiptDate;
    private BigDecimal amount;
    private BigDecimal dpp;
    private BigDecimal ppn;
    private BigDecimal serviceCharge;
    private String paymentType;
}
