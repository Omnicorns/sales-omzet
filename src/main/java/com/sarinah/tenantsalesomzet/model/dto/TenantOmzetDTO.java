package com.sarinah.tenantsalesomzet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantOmzetDTO {
    private String tenantId;
    private String tenantName;
    private String brandName;
    private String lotLocation;
    private Date salesDate;
    private String day;
    private String channel;
    private BigDecimal totalOmzet;
    private List<TenantOmzetReceiptDTO> receipts;
}
