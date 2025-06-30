package com.sarinah.tenantsalesomzet.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostTenantOmzetRequest {
    private String tenantName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date salesDate;
    private String brandName;
    private String day;
    private String lotLocation;
    private BigDecimal omzet;
    private List<ReceiptItem> receiptList;
    private String accessToken;

}
