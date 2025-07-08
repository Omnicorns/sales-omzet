package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;
import static com.sarinah.tenantsalesomzet.util.Constant.ERR_MSG_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE;


@Service
public class ValidateTenantOmzetService {
    public ValidationResponse execute (PostTenantOmzetRequest input){
        this.doFilterSalesDate(input);
        this.doFilterReceiptList(input);

        return ValidationResponse.builder().result(true).build();
    }

    private void doFilterSalesDate(PostTenantOmzetRequest input) {
        Date salesDate = input.getSalesDate();

        // 1. Cek null
        if (salesDate == null) {
            throw new BusinessException(ERROR_CODE_30000,"request invalid");

        }

        // 2. (Opsional) cek range, misal jangan di-future:

    }

    private void doFilterReceiptList(PostTenantOmzetRequest input) {
        List<ReceiptItem> receiptList = input.getReceiptList();
        if (receiptList == null || receiptList.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000,"Receipt list cannot be empty");


        }
        // (opsional) validasi tiap item:
        for (ReceiptItem r : receiptList) {
            if (r.getReceiptNumber() == null || r.getReceiptNumber().isBlank()) {
                throw new BusinessException(ERROR_CODE_30000,"Receipt Number cannot be empty");
            }
            // ... validasi field lain jika perlu
        }
    }







}