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
import java.util.stream.Collectors;


@Log4j2
@RequiredArgsConstructor
@Service
public class PostTenantOmzetService {

    private final TenantOmzetRepository omzetRepository;
    private final ValidateTenantOmzetService validateTenantOmzetService;

    public ValidationResponse execute(PostTenantOmzetRequest input) {
        if (validateTenantOmzetService.execute(input).getResult()) {
            ApiContext ctx    = ApiContextHolder.getContext();
            String    tenant  = ctx.getTenantName();
            String    brand   = ctx.getTenantBrand();
            ZoneId    zone    = ZoneId.of("Asia/Jakarta"); // atau systemDefault()

            for (ReceiptItem r : input.getReceiptList()) {
                // 1. Konversi receiptDate → LocalDate + LocalDateTime
                Instant inst = r.getReceiptDate().toInstant();
                LocalDateTime ldt  = LocalDateTime.ofInstant(inst, zone);
                LocalDate     date = ldt.toLocalDate();

                // 2. Nama hari (ID)
                String namaHari = ldt
                        .getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, new Locale("id", "ID"));

                // 3. Normalisasi ke start-of-day → Timestamp salesDate
                Timestamp salesDate = Timestamp.valueOf(date.atStartOfDay());

                // 4. Cari atau buat TenantOmzet untuk (tenant, salesDate)
                TenantOmzet tenantOmzet = omzetRepository
                        .findByTenantNameAndSalesDate(tenant, salesDate)
                        .orElseGet(() -> {
                            TenantOmzet t = new TenantOmzet();
                            t.setTenantId     (UUID.randomUUID().toString());
                            t.setLotLocation  (input.getLotLocation());
                            t.setSalesDate    (salesDate);
                            t.setDay          (namaHari);
                            t.setCreatedBy    ("SYSTEM");
                            t.setCreatedTime  (new Timestamp(System.currentTimeMillis()));
                            t.setTenantName   (tenant);
                            t.setBrandName    (brand);
                            t.setReceipts     (new ArrayList<>());   // inisialisasi list
                            return t;
                        });

                // 5. Update metadata parent
                tenantOmzet.setLotLocation (input.getLotLocation());
                tenantOmzet.setDay         (namaHari);
                tenantOmzet.setUpdatedBy   ("SYSTEM");
                tenantOmzet.setUpdatedTime (new Timestamp(System.currentTimeMillis()));

                // 6. Append ReceiptItem ke TenantOmzet jika belum ada
                List<TenantOmzetReceipt> existing = tenantOmzet.getReceipts();
                boolean exists = existing.stream().anyMatch(rc ->
                        rc.getReceiptNumber().equals(r.getReceiptNumber())
                                && rc.getAmount().compareTo(r.getAmountAsBigDecimal()) == 0
                );
                if (!exists) {
                    TenantOmzetReceipt nr = new TenantOmzetReceipt();
                    nr.setId             (UUID.randomUUID().toString());
                    nr.setReceiptNumber  (r.getReceiptNumber());
                    Date dateWithTime = r.getReceiptDate();      // pastikan ini bukan java.sql.Date
                    Timestamp ts = new Timestamp(dateWithTime.getTime());
                    nr.setReceiptDate(ts);
                    r.setServiceCharge  (r.getServiceChargeAsBigDecimal());
                    nr.setDpp            (r.getDppAsBigDecimal());
                    nr.setPpn            (r.getPpnAsBigDecimal());
                    nr.setAmount         (r.getAmountAsBigDecimal());
                    nr.setPaymentType    (r.getPaymentType());
                    nr.setTenantOmzet    (tenantOmzet);
                    nr.setUpdatedTime    (new Timestamp(System.currentTimeMillis()));
                    existing.add(nr);
                }

                // 7. Hitung total omzet unik dan set ke parent
                BigDecimal totalOmzet = existing.stream()
                        .collect(Collectors.groupingBy(
                                TenantOmzetReceipt::getReceiptNumber,
                                Collectors.mapping(TenantOmzetReceipt::getAmount, Collectors.toSet())
                        ))
                        .values().stream()
                        .flatMap(Set::stream)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                tenantOmzet.setOmzet(totalOmzet);

                // 8. Simpan (baru atau update)
                omzetRepository.save(tenantOmzet);
            }
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


