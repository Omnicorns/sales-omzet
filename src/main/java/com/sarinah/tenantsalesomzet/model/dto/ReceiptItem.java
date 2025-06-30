package com.sarinah.tenantsalesomzet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItem {
    private String receiptNumber;
    private BigDecimal amount;
    private String paymentType;

}
