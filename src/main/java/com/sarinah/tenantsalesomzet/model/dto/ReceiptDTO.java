package com.sarinah.tenantsalesomzet.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private String receiptNumber;
    private Object amount;
    private Object dpp;
    private Object ppn;
    private Object serviceCharge;
    private String paymentType;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date receiptDate;
}
