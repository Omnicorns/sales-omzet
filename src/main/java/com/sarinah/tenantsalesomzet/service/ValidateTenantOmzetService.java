package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;



@Service
public class ValidateTenantOmzetService {

    public ValidationResponse execute (PostTenantOmzetRequest input){
        this.doFilterReceiptList(input);
        return ValidationResponse.builder().result(true).build();
    }

    private void doFilterReceiptList(PostTenantOmzetRequest input) {
        List<ReceiptItem> receiptList = input.getReceiptList();
        if (receiptList == null || receiptList.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000, "Receipt list tidak boleh  kosong");
        }
        // (opsional) validasi tiap item:
        for (ReceiptItem r : receiptList) {
            if (r.getReceiptNumber() == null || r.getReceiptNumber().isBlank()) {
                throw new BusinessException(ERROR_CODE_30000, "Receipt Number tidak boleh kosong");
            }
            Date date = r.getReceiptDate();
            if (date == null){
                throw new BusinessException(ERROR_CODE_30000, "Tanggal Receipt tidak boleh kosong");
            }

            LocalDate today = LocalDate.now();
            LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
            Date endOfTodayDate = Date.from(endOfToday.atZone(ZoneId.systemDefault()).toInstant());
            if (date.after(endOfTodayDate)){
                throw new BusinessException(ERROR_CODE_30000, "Tanggal input receipt tidak boleh lebih dari hari ini");
            }
        }
    }

}