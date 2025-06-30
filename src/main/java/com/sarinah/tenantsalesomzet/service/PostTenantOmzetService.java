package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceipt;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetReceiptRepository;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetRepository;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Service
public class PostTenantOmzetService {

    private final ValidateTenantOmzetService validateTenantOmzetService;
    private final TenantOmzetRepository omzetRepository;
    private final TenantOmzetReceiptRepository tenantOmzetReceiptRepository;


    public ValidationResponse execute(PostTenantOmzetRequest input) {
        if (validateTenantOmzetService.execute(input).getResult()) {
            TenantOmzet tenantOmzet = new TenantOmzet();
            tenantOmzet.setTenantId(UUID.randomUUID().toString());
            tenantOmzet.setBrandName(input.getBrandName());
            tenantOmzet.setLotLocation(input.getLotLocation());
            tenantOmzet.setSalesDate((input.getSalesDate()));
            tenantOmzet.setTenantName(input.getTenantName());
            tenantOmzet.setOmzet(input.getOmzet());
            tenantOmzet.setDay(input.getDay());
            tenantOmzet.setCreatedBy("SYSTEM");
            tenantOmzet.setUpdatedBy("SYSTEM");
            tenantOmzet.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            tenantOmzet.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            List<TenantOmzetReceipt> finalReceipts = new ArrayList<>();
            for (ReceiptItem r : input.getReceiptList()) {
                TenantOmzetReceipt receipt = tenantOmzetReceiptRepository
                        .findByReceiptNumber(r.getReceiptNumber())
                        .map(existing -> {
                            // Update yang lama
                            existing.setAmount(r.getAmount());
                            existing.setPaymentType(r.getPaymentType());
                            existing.setTenantOmzet(tenantOmzet);
                            return existing;
                        })
                        .orElseGet(() -> {
                            // Buat baru
                            TenantOmzetReceipt newReceipt = new TenantOmzetReceipt();
                            newReceipt.setId(UUID.randomUUID().toString());
                            newReceipt.setReceiptNumber(r.getReceiptNumber());
                            newReceipt.setAmount(r.getAmount());
                            newReceipt.setPaymentType(r.getPaymentType());
                            newReceipt.setTenantOmzet(tenantOmzet);
                            return newReceipt;
                        });

                finalReceipts.add(receipt);
            }
            tenantOmzet.setReceipts(finalReceipts);
            this.omzetRepository.save(tenantOmzet);

        }
        return ValidationResponse.builder().result(true).build();
    }
}
