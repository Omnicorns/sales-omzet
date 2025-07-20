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
        cal.setTime(endDate);
        cal.add(Calendar.DATE, 1);
        Date inclusiveEndDate = cal.getTime();
        Page<TenantOmzet> page = tenantOmzetRepository
                .findBySalesDateBetween(startDate, inclusiveEndDate, pageable);

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
