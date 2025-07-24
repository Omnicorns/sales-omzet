package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;
import static com.sarinah.tenantsalesomzet.util.Constant.ERR_MSG_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE;


@Service
public class ValidateTenantOmzetService {
    private static final String[] DATE_PATTERNS = {
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm",
            "dd-MM-yyyy"
    };
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

    }
    }

}