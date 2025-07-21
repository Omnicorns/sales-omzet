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
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;


@Log4j2
@RequiredArgsConstructor
@Service
public class PostTenantOmzetService {

    private final TenantOmzetRepository omzetRepository;
    private final ValidateTenantOmzetService validateTenantOmzetService;

    public ValidationResponse execute(PostTenantOmzetRequest input) {
        if (validateTenantOmzetService.execute(input).getResult()) {
            LocalDateTime localDateTime = null;
            String namaHari = null;
            if (!input.getReceiptList().isEmpty()) {
                ReceiptItem firstReceipt = input.getReceiptList().get(0);
                Date dateFromRequest = firstReceipt.getReceiptDate(); // hasil parsing setter
                Instant instant = dateFromRequest.toInstant();
                ZoneId zone = ZoneId.systemDefault(); // atau ZoneId.of("Asia/Jakarta")
                localDateTime = LocalDateTime.ofInstant(instant, zone);
                namaHari = localDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));


            }
            ApiContext ctx = ApiContextHolder.getContext();
            String tenant = ctx.getTenantName();
            String brand = ctx.getTenantBrand();
            String finalNamaHari = namaHari;

            LocalDateTime normalizedDate = toStartOfDay(localDateTime); // dari receipt pertama
            Timestamp salesDate = Timestamp.valueOf(normalizedDate);
            TenantOmzet tenantOmzet = omzetRepository
                    .findByTenantNameAndSalesDate(tenant, salesDate)
                    .orElseGet(() -> {
                        TenantOmzet t = new TenantOmzet();
                        t.setTenantId(UUID.randomUUID().toString());
                        t.setLotLocation(input.getLotLocation());
                        t.setSalesDate(salesDate);
                        t.setDay(finalNamaHari); // Atau sesuaikan format
                        t.setCreatedBy("SYSTEM");
                        t.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                        t.setTenantName(tenant);
                        t.setBrandName(brand);
                        // receipts sudah di‐inisialisasi di entitas
                        return t;
                    });
            // Set salesDate dan day dari receipt pertama (jika ada)


            tenantOmzet.setUpdatedBy("SYSTEM");
            tenantOmzet.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

            // 2. Pastikan koleksi receipts sudah ter‐load
            List<TenantOmzetReceipt> existingReceipts = tenantOmzet.getReceipts();

            // 3. Loop input, hanya append child baru jika belum ada nomor+amount yang sama
            for (ReceiptItem r : input.getReceiptList()) {
                boolean exists = existingReceipts.stream().anyMatch(rc ->
                        rc.getReceiptNumber().equals(r.getReceiptNumber())
                                && rc.getAmount().compareTo(r.getAmountAsBigDecimal()) == 0
                );

                if (!exists) {
                    TenantOmzetReceipt nr = new TenantOmzetReceipt();
                    Date dateOnly = r.getReceiptDate();
                    Instant instant = dateOnly.toInstant();
                    ZoneId zone = ZoneId.systemDefault();
                    LocalDateTime combined = LocalDateTime.ofInstant(instant, zone);

// buat LocalDateTime dengan waktu sekarang
                    nr.setReceiptDate(Timestamp.valueOf(combined));
                    nr.setId(UUID.randomUUID().toString());
                    nr.setReceiptNumber(r.getReceiptNumber());
                    nr.setServiceCharge(r.getServiceChargeAsBigDecimal());
                    nr.setDpp(r.getDppAsBigDecimal());
                    nr.setPpn(r.getPpnAsBigDecimal());
                    nr.setAmount(r.getAmountAsBigDecimal());
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
                .message("Data omzet berhasil disimpan.")
                .build();
    }

    private LocalDateTime toStartOfDay(LocalDateTime dt) {
        return dt.toLocalDate().atStartOfDay();
    }

}


