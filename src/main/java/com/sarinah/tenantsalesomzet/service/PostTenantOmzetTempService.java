package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration.ApiContextHolder;
import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.model.ApiContext;
import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;

import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceiptTmp;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetTmp;

import com.sarinah.tenantsalesomzet.repository.TenantOmzetTmpRepository;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import com.sarinah.tenantsalesomzet.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;

@Log4j2
@RequiredArgsConstructor
@Service
public class PostTenantOmzetTempService {
    private final TenantOmzetTmpRepository omzetRepository;
    private final ValidateTenantOmzetService validateTenantOmzetService;
    private static final String SEP_REGEX =";";
    private static final String SEP_OUT = ","; // separator "|"

    public ValidationResponse execute(PostTenantOmzetRequest input) {
        if (validateTenantOmzetService.execute(input).getResult()) {
            ApiContext ctx    = ApiContextHolder.getContext();
            String    tenant  = ctx.getTenantName();
            String    brand   = ctx.getTenantBrand();
            ZoneId zone    = ZoneId.of("Asia/Jakarta"); // atau systemDefault()

            for (ReceiptItem r : input.getReceiptList()) {
                // 1. Konversi receiptDate → LocalDate + LocalDateTime
                Instant inst = r.getReceiptDate().toInstant();
                LocalDateTime ldt  = LocalDateTime.ofInstant(inst, zone);
                LocalDate date = ldt.toLocalDate();

                // 2. Nama hari (ID)
                String namaHari = ldt
                        .getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, new Locale("id", "ID"));

                // 3. Normalisasi ke start-of-day → Timestamp salesDate
                Timestamp salesDate = Timestamp.valueOf(date.atStartOfDay());

                // 4. Cari atau buat TenantOmzet untuk (tenant, salesDate)
                TenantOmzetTmp tenantOmzet = omzetRepository
                        .findByTenantNameAndSalesDate(tenant, salesDate)
                        .orElseGet(() -> {
                            TenantOmzetTmp t = new TenantOmzetTmp();
                            t.setTenantId     (UUID.randomUUID().toString());
                            t.setLotLocation  (input.getLotLocation());
                            t.setSalesDate    (salesDate);
                            t.setDay          (namaHari);
                            t.setCreatedBy    ("SYSTEM");
                            t.setCreatedTime  (new Timestamp(System.currentTimeMillis()));
                            t.setTenantName   (tenant);
                            t.setBrandName    (brand);
                            t.setReceipts     (new ArrayList<>());
                            t.setApprovalStatus(TenantOmzetTmp.ApprovalStatus.PENDING);
                            t.setSubmittedBy("SYSTEM"); // atau ctx username
                            t.setSubmittedTime(new Timestamp(System.currentTimeMillis()));// inisialisasi list
                            return t;
                        });

                Constant.CHANNEL status = Constant.CHANNEL.valueOf(r.getChannel());

                // 5. Update metadata parent
                tenantOmzet.setLotLocation (input.getLotLocation());
                tenantOmzet.setDay         (namaHari);
                tenantOmzet.setUpdatedBy   ("SYSTEM");
                tenantOmzet.setChannelName(String.valueOf(status));
                tenantOmzet.setUpdatedTime (new Timestamp(System.currentTimeMillis()));

                // 6. Append ReceiptItem ke TenantOmzet jika belum ada
                List<TenantOmzetReceiptTmp> existing = tenantOmzet.getReceipts();


                String paymentTypeDesc = normalizePaymentTypeDesc(r.getPaymentType());


                if (paymentTypeDesc == null){
                    throw new BusinessException(ERROR_CODE_30000, "payment type tidak valid");
                }

                // cari receipt dengan nomor yang sama
                Optional<TenantOmzetReceiptTmp> sameNumber = existing.stream()
                        .filter(rc -> rc.getReceiptNumber().equals(r.getReceiptNumber()))
                        .findFirst();

                Timestamp ts = new Timestamp(r.getReceiptDate().getTime());

                if (sameNumber.isPresent()) {
                    // sudah ada: kalau amount beda → replace field-fieldnya
                    TenantOmzetReceiptTmp rc = sameNumber.get();
                    if (rc.getAmount() == null || rc.getAmount().compareTo(r.getAmountAsBigDecimal()) != 0) {
                        rc.setServiceCharge (r.getServiceChargeAsBigDecimal());
                        rc.setDpp           (r.getDppAsBigDecimal());
                        rc.setPpn           (r.getPpnAsBigDecimal());
                        rc.setAmount        (r.getAmountAsBigDecimal());
                        rc.setPaymentType   (paymentTypeDesc);
                        rc.setReceiptDate   (ts);
                        rc.setUpdatedTime   (new Timestamp(System.currentTimeMillis()));
                    }
                    // kalau amount sama → idempotent (tidak melakukan apa-apa)
                } else {
                    // belum ada: tambah baru
                    TenantOmzetReceiptTmp nr = new TenantOmzetReceiptTmp();
                    nr.setId            (UUID.randomUUID().toString());
                    nr.setReceiptNumber (r.getReceiptNumber());
                    nr.setReceiptDate   (ts);
                    nr.setServiceCharge (r.getServiceChargeAsBigDecimal());
                    nr.setDpp           (r.getDppAsBigDecimal());
                    nr.setPpn           (r.getPpnAsBigDecimal());
                    nr.setAmount        (r.getAmountAsBigDecimal());
                    nr.setPaymentType   (paymentTypeDesc);
                    nr.setTenantOmzet   (tenantOmzet);
                    nr.setUpdatedTime   (new Timestamp(System.currentTimeMillis()));
                    existing.add(nr);
                }

                // 7. Hitung total omzet unik dan set ke parent
                BigDecimal totalOmzet = existing.stream()
                        .collect(Collectors.groupingBy(
                                TenantOmzetReceiptTmp::getReceiptNumber,
                                Collectors.mapping(TenantOmzetReceiptTmp::getAmount, Collectors.toSet())
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

    private List<Constant.PAYMENT_STATUS> parseTypes(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException(ERROR_CODE_30000, "payment type kosong");
        }

        return Arrays.stream(raw.split(SEP_REGEX))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(code -> {
                    Constant.PAYMENT_STATUS p = null;
                    try {
                        Integer v = Integer.parseInt(code);
                        p = Constant.PAYMENT_STATUS.fromValue(v); // ini method kamu: valueOf(int)
                    } catch (NumberFormatException ex) {
                        // kalau bukan angka
                    }

                    if (p == null) {
                        throw new BusinessException(ERROR_CODE_30000, "payment type tidak valid: " + code);
                    }
                    return p;
                })
                .distinct()
                .sorted(Comparator.comparingInt(Constant.PAYMENT_STATUS::getValue)) // konsisten berdasar angka
                .toList();
    }

    private String normalizePaymentTypeDesc(String raw) {
        List<Constant.PAYMENT_STATUS> types = parseTypes(raw);
        return types.stream()
                .map(Constant.PAYMENT_STATUS::getDesc)
                .distinct()
                .collect(java.util.stream.Collectors.joining(SEP_OUT));
    }
}
