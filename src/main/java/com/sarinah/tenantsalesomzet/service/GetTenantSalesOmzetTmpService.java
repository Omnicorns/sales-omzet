package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.model.dto.ReceiptDTO;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetTmp;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetTmpRepository;
import com.sarinah.tenantsalesomzet.response.TenantOmzetResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTenantSalesOmzetTmpService {
    private final TenantOmzetTmpRepository tenantOmzetRepository;


    @Transactional
    public Page<TenantOmzetResponse> execute(Date startDate, Date endDate, boolean onlyTodayUpdate, String brandName, Pageable pageable) {
        Date todayStart = null;
        Date tomorrowStart = null;

        if (onlyTodayUpdate) {
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            todayStart = todayCal.getTime();

            todayCal.add(Calendar.DAY_OF_MONTH, 1);
            tomorrowStart = todayCal.getTime();
        }

        Page<TenantOmzetTmp> page;

        if (onlyTodayUpdate) {
            // Filter tambahan updateTime untuk hari ini + brandName
            if (brandName != null && !brandName.trim().isEmpty()) {
                page = tenantOmzetRepository.findByUpdatedTimeBetweenAndBrandName(
                        todayStart, tomorrowStart, brandName.trim(), pageable);
            } else {
                page = tenantOmzetRepository.findByUpdatedTimeBetween(
                        todayStart, tomorrowStart, pageable);
            }
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date normalizedStart = cal.getTime();

            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date normalizedEnd = cal.getTime();

            // Filter berdasarkan date range + brandName
            if (brandName != null && !brandName.trim().isEmpty()) {
                page = tenantOmzetRepository
                        .findBySalesDateGreaterThanEqualAndSalesDateLessThanAndBrandName(
                                normalizedStart, normalizedEnd, brandName.trim(), pageable
                        );
            } else {
                page = tenantOmzetRepository
                        .findBySalesDateGreaterThanEqualAndSalesDateLessThan(
                                normalizedStart, normalizedEnd, pageable
                        );
            }
        }

        return page.map(this::mapToResponse);
    }




    private TenantOmzetResponse mapToResponse(TenantOmzetTmp entity) {
        return TenantOmzetResponse.builder()
                .tenantId(entity.getTenantId())
                .tenantName(entity.getTenantName())
                .brandName(entity.getBrandName())
                .lotLocation(entity.getLotLocation())
                .salesDate(entity.getSalesDate())
                .day(entity.getDay())
                .channel(entity.getChannelName())
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
