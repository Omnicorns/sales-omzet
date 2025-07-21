package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.model.dto.ReceiptDTO;
import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetRepository;
import com.sarinah.tenantsalesomzet.response.TenantOmzetResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTenantSalesOmzetService {
    private final TenantOmzetRepository tenantOmzetRepository;

    @Transactional
    public Page<TenantOmzetResponse>  execute(Date startDate, Date endDate, Pageable pageable) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date normalizedStart = cal.getTime();

        // Normalisasi endDate ke awal hari berikutnya
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1); // Supaya jam berapapun di endDate ikut
        Date normalizedEnd = cal.getTime();
        Page<TenantOmzet> page = tenantOmzetRepository
                .findBySalesDateGreaterThanEqualAndSalesDateLessThan(normalizedStart, normalizedEnd, pageable);

        return page.map(this::mapToResponse);
    }

    private TenantOmzetResponse mapToResponse(TenantOmzet entity) {
        return TenantOmzetResponse.builder()
                .tenantId(entity.getTenantId())
                .tenantName(entity.getTenantName())
                .brandName(entity.getBrandName())
                .lotLocation(entity.getLotLocation())
                .salesDate(entity.getSalesDate())
                .day(entity.getDay())
                .totalOmzet(entity.getOmzet())
                .updatedTime(entity.getUpdatedTime())
                .receipts(entity.getReceipts().stream().
                         map(r -> ReceiptDTO.builder()
                        .receiptNumber(r.getReceiptNumber())
                        .amount(r.getAmount())
                        .ppn(r.getPpn())
                        .dpp(r.getDpp())
                        .serviceCharge(r.getServiceCharge())
                        .paymentType(r.getPaymentType())
                        .receiptDate(r.getReceiptDate())
                        .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
