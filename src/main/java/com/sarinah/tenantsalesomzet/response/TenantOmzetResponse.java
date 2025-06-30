package com.sarinah.tenantsalesomzet.response;

import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class TenantOmzetResponse {
    private String tenantId;
    private String tenantName;
    private String brandName;
    private String lotLocation;
    private Date salesDate;
    private String day;
    private BigDecimal totalOmzet;
    private List<ReceiptItem> receipts;
}
