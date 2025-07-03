package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.model.dto.ReceiptItem;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetRepository;
import com.sarinah.tenantsalesomzet.response.TenantOmzetResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTenantSalesOmzetService {
    private final TenantOmzetRepository tenantOmzetRepository;

    @Transactional
    public Page<TenantOmzetResponse>  execute(Date startDate, Date endDate, Pageable pageable) {
        Page<TenantOmzet> page = tenantOmzetRepository
                .findBySalesDateBetween(startDate, endDate, pageable);

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
                .receipts(entity.getReceipts().stream().map(r -> ReceiptItem.builder()
                        .receiptNumber(r.getReceiptNumber())
                        .amount(r.getAmount())
                        .paymentType(r.getPaymentType())
                        .ReceiptDate(r.getReceiptDate())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
