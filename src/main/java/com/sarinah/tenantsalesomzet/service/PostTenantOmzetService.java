package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration.ApiContextHolder;
import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.model.ApiContext;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceipt;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetRepository;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


@Log4j2
@RequiredArgsConstructor
@Service
public class PostTenantOmzetService {

    private final TenantOmzetRepository omzetRepository;
    private final ValidateTenantOmzetService validateTenantOmzetService;

    public ValidationResponse execute(PostTenantOmzetRequest input) {
        if (validateTenantOmzetService.execute(input).getResult()) {
            ApiContext ctx = ApiContextHolder.getContext();
            String tenant = ctx.getTenantName();
            String brand = ctx.getTenantBrand();
            TenantOmzet tenantOmzet = omzetRepository
                    .findByTenantNameAndSalesDate(tenant, input.getSalesDate())
                    .orElseGet(() -> {
                        TenantOmzet t = new TenantOmzet();
                        t.setTenantId(UUID.randomUUID().toString());
                        t.setLotLocation(input.getLotLocation());
                        t.setSalesDate(input.getSalesDate());
                        t.setDay(input.getDay());
                        t.setCreatedBy("SYSTEM");
                        t.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                        t.setTenantName(tenant);
                        t.setBrandName(brand);
                        // receipts sudah di‐inisialisasi di entitas
                        return t;
                    });
            tenantOmzet.setUpdatedBy("SYSTEM");
            tenantOmzet.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

            // 2. Pastikan koleksi receipts sudah ter‐load
            List<TenantOmzetReceipt> existingReceipts = tenantOmzet.getReceipts();

            // 3. Loop input, hanya append child baru jika belum ada nomor+amount yang sama
            for (ReceiptItem r : input.getReceiptList()) {
                boolean exists = existingReceipts.stream().anyMatch(rc ->
                        rc.getReceiptNumber().equals(r.getReceiptNumber())
                                && rc.getAmount().compareTo(r.getAmount()) == 0
                );

                if (!exists) {
                    TenantOmzetReceipt nr = new TenantOmzetReceipt();
                    Date dateOnly = r.getReceiptDate();

// 1. Konversi ke LocalDate (tanpa zona kalau di server sudah sesuai Jakarta)
                    LocalDate localDate = dateOnly.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

// 2. Ambil waktu sekarang
                    LocalTime currentTime = LocalTime.now();

// 3. Gabungkan jadi LocalDateTime
                    LocalDateTime combined = LocalDateTime.of(localDate, currentTime);

// buat LocalDateTime dengan waktu sekarang
                    nr.setReceiptDate(Timestamp.valueOf(combined));
                    nr.setId(UUID.randomUUID().toString());
                    nr.setReceiptNumber(r.getReceiptNumber());
                    nr.setAmount(r.getAmount());
                    nr.setPaymentType(r.getPaymentType());
                    nr.setTenantOmzet(tenantOmzet);
                    nr.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

                    existingReceipts.add(nr);
                }
            }

            // 4. Hitung total omzet unik (sum of distinct amounts per receiptNumber)
            Map<String, Set<BigDecimal>> receiptAmountMap = new HashMap<>();
            for (TenantOmzetReceipt rc : existingReceipts) {
                receiptAmountMap
                        .computeIfAbsent(rc.getReceiptNumber(), k -> new HashSet<>())
                        .add(rc.getAmount());
            }
            BigDecimal totalOmzet = receiptAmountMap.values().stream()
                    .flatMap(Set::stream)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            tenantOmzet.setOmzet(totalOmzet);

            // 5. Simpan header beserta semua child (only new ones get INSERTed)
            omzetRepository.save(tenantOmzet);
        }
            return ValidationResponse.builder()
                    .result(true)
                    .build();
        }

    }
