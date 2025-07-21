package com.sarinah.tenantsalesomzet.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptDTO;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date salesDate;
    private String day;
    private BigDecimal totalOmzet;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date updatedTime;

    private List<ReceiptDTO> receipts;
}
