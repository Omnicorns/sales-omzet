package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceipt;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceiptTmp;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetTmp;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetRepository;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetTmpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Slf4j
public class ApproveTenantOmzetService {

    private final TenantOmzetTmpRepository tmpRepo;
    private final TenantOmzetRepository mainRepo;

    @Transactional
    public void approveOne(String tmpId, String approvedBy) {
        log.info("=== START APPROVAL: tmpId={}, approvedBy={} ===", tmpId, approvedBy);

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 1. Load TMP (lock row)
        TenantOmzetTmp tmp = tmpRepo.findByIdForUpdate(tmpId)
                .orElseThrow(() -> new BusinessException("30000", "Data tmp tidak ditemukan"));

        int tmpReceiptSize = (tmp.getReceipts() == null) ? 0 : tmp.getReceipts().size();
        log.info("TMP loaded: status={}, receipts={}", tmp.getApprovalStatus(), tmpReceiptSize);

        // 2. Re-approval aware idempotent check
        TenantOmzetTmp.ApprovalStatus status = tmp.getApprovalStatus();
        boolean alreadyApproved = status == TenantOmzetTmp.ApprovalStatus.APPROVED;

        if (alreadyApproved) {
            Timestamp approvedTime = tmp.getApprovedTime();

            boolean hasChangeAfterApprove;
            if (approvedTime == null) {
                // data lama / edge case -> lebih aman proses ulang
                hasChangeAfterApprove = true;
            } else if (tmp.getReceipts() == null || tmp.getReceipts().isEmpty()) {
                hasChangeAfterApprove = false;
            } else {
                hasChangeAfterApprove = tmp.getReceipts().stream().anyMatch(r -> {
                    Timestamp ut = r.getUpdatedTime();
                    // kalau updatedTime null, treat as changed biar nggak miss data
                    return ut == null || ut.after(approvedTime);
                });
            }

            if (!hasChangeAfterApprove) {
                log.info("TMP already approved and no receipt updated after approvedTime={}, skipping", approvedTime);
                return;
            }

            log.info("TMP already approved but has receipt changes after approvedTime -> re-approving");
            // Treat as re-approval: turunkan status in-memory agar lolos validasi
            tmp.setApprovalStatus(TenantOmzetTmp.ApprovalStatus.PENDING);
        }

        // 3. Status validation
        if (tmp.getApprovalStatus() != TenantOmzetTmp.ApprovalStatus.PENDING) {
            throw new BusinessException("30000",
                    "Status bukan PENDING (current=" + tmp.getApprovalStatus() + ")");
        }

        // 4. Load or create MAIN
        TenantOmzet main = mainRepo
                .findByTenantNameAndSalesDateAndChannelNameAndLotLocation(
                        tmp.getTenantName(),
                        tmp.getSalesDate(),
                        tmp.getChannelName(),
                        tmp.getLotLocation()
                )
                .orElseGet(() -> {
                    log.info("Creating new TenantOmzet");
                    TenantOmzet m = new TenantOmzet();
                    m.setTenantId(UUID.randomUUID().toString());
                    m.setTenantName(tmp.getTenantName());
                    m.setSalesDate(tmp.getSalesDate());
                    m.setReceipts(new ArrayList<>());
                    m.setCreatedBy(approvedBy);
                    m.setCreatedTime(now);
                    log.info("New TenantOmzet created: tenantId={}", m.getTenantId());
                    return m;
                });

        int mainReceiptSize = (main.getReceipts() == null) ? 0 : main.getReceipts().size();
        log.info("Main entity: tenantId={}, existing receipts={}", main.getTenantId(), mainReceiptSize);

        // 5. Update parent fields
        main.setLotLocation(tmp.getLotLocation());
        main.setDay(tmp.getDay());
        main.setChannelName(tmp.getChannelName());
        main.setBrandName(tmp.getBrandName());
        main.setOmzet(tmp.getOmzet());
        main.setUpdatedBy(approvedBy);
        main.setUpdatedTime(now);

        // Ensure receipts list not null
        if (main.getReceipts() == null) main.setReceipts(new ArrayList<>());

        // 6. Merge receipts
        Map<String, TenantOmzetReceipt> mainReceiptMap = main.getReceipts().stream()
                .filter(r -> r.getReceiptNumber() != null)
                .collect(Collectors.toMap(
                        TenantOmzetReceipt::getReceiptNumber,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        log.info("Processing {} TMP receipts", tmpReceiptSize);

        int newCount = 0, updateCount = 0;
        if (tmp.getReceipts() != null) {
            for (TenantOmzetReceiptTmp tmpReceipt : tmp.getReceipts()) {
                if (tmpReceipt == null) continue;

                String receiptNumber = tmpReceipt.getReceiptNumber();
                if (receiptNumber == null || receiptNumber.isBlank()) {
                    log.warn("Skipping TMP receipt with empty receiptNumber (tmpId={})", tmpId);
                    continue;
                }

                TenantOmzetReceipt mainReceipt = mainReceiptMap.get(receiptNumber);

                if (mainReceipt == null) {
                    // Create new
                    mainReceipt = new TenantOmzetReceipt();
                    mainReceipt.setId(UUID.randomUUID().toString());
                    mainReceipt.setReceiptNumber(receiptNumber);
                    mainReceipt.setTenantOmzet(main);     // ✅ Set parent
                    mainReceipt.setCreatedTime(now);

                    main.getReceipts().add(mainReceipt); // ✅ Add to collection
                    mainReceiptMap.put(receiptNumber, mainReceipt);

                    log.info("Creating new receipt: id={}, receiptNumber={}",
                            mainReceipt.getId(), mainReceipt.getReceiptNumber());
                    newCount++;
                } else {
                    log.info("Updating existing receipt: {}", mainReceipt.getReceiptNumber());
                    updateCount++;
                }

                // Update fields
                mainReceipt.setReceiptDate(tmpReceipt.getReceiptDate());
                mainReceipt.setServiceCharge(tmpReceipt.getServiceCharge());
                mainReceipt.setDpp(tmpReceipt.getDpp());
                mainReceipt.setPpn(tmpReceipt.getPpn());
                mainReceipt.setAmount(tmpReceipt.getAmount());
                mainReceipt.setPaymentType(tmpReceipt.getPaymentType());
                mainReceipt.setUpdatedBy(approvedBy);
                mainReceipt.setUpdatedTime(now);
            }
        }

        log.info("Receipt processing: new={}, updated={}, total={}",
                newCount, updateCount, main.getReceipts().size());

        // 7. Save MAIN
        try {
            log.info("Saving main entity...");
            TenantOmzet saved = mainRepo.save(main);
            mainRepo.flush();  // ✅ Force flush untuk debug
            int savedReceiptSize = (saved.getReceipts() == null) ? 0 : saved.getReceipts().size();
            log.info("Main entity saved: tenantId={}, receipts={}", saved.getTenantId(), savedReceiptSize);
        } catch (Exception e) {
            log.error("ERROR saving main entity: ", e);
            throw e;
        }

        // 8. Update TMP status (APPROVED)
        try {
            log.info("Updating TMP status to APPROVED...");
            tmp.setApprovalStatus(TenantOmzetTmp.ApprovalStatus.APPROVED);
            tmp.setApprovedBy(approvedBy);
            tmp.setApprovedTime(now);
            tmp.setUpdatedTime(now);
            tmpRepo.save(tmp);
            tmpRepo.flush();  // ✅ Force flush
            log.info("TMP status updated successfully");
        } catch (Exception e) {
            log.error("ERROR updating TMP status: ", e);
            throw e;
        }

        log.info("=== APPROVAL COMPLETED ===");
    }
}