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

        // 1. Load TMP
        TenantOmzetTmp tmp = tmpRepo.findByIdForUpdate(tmpId)
                .orElseThrow(() -> new BusinessException("30000", "Data tmp tidak ditemukan"));
        log.info("TMP loaded: status={}, receipts={}",
                tmp.getApprovalStatus(), tmp.getReceipts().size());

        // 2. Idempotent check
        if (tmp.getApprovalStatus() == TenantOmzetTmp.ApprovalStatus.APPROVED) {
            log.info("TMP already approved, skipping");
            return;
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

        log.info("Main entity: tenantId={}, existing receipts={}",
                main.getTenantId(), main.getReceipts().size());

        // 5. Update parent fields
        main.setLotLocation(tmp.getLotLocation());
        main.setDay(tmp.getDay());
        main.setChannelName(tmp.getChannelName());
        main.setBrandName(tmp.getBrandName());
        main.setOmzet(tmp.getOmzet());
        main.setUpdatedBy(approvedBy);
        main.setUpdatedTime(now);

        // 6. Merge receipts
        Map<String, TenantOmzetReceipt> mainReceiptMap = main.getReceipts().stream()
                .collect(Collectors.toMap(
                        TenantOmzetReceipt::getReceiptNumber,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        log.info("Processing {} TMP receipts", tmp.getReceipts().size());

        int newCount = 0, updateCount = 0;
        for (TenantOmzetReceiptTmp tmpReceipt : tmp.getReceipts()) {
            TenantOmzetReceipt mainReceipt = mainReceiptMap.get(tmpReceipt.getReceiptNumber());

            if (mainReceipt == null) {
                // Create new
                mainReceipt = new TenantOmzetReceipt();
                mainReceipt.setId(UUID.randomUUID().toString());
                mainReceipt.setReceiptNumber(tmpReceipt.getReceiptNumber());
                mainReceipt.setTenantOmzet(main);  // ✅ Set parent
                mainReceipt.setCreatedTime(now);
                main.getReceipts().add(mainReceipt);  // ✅ Add to collection
                mainReceiptMap.put(mainReceipt.getReceiptNumber(), mainReceipt);

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

        log.info("Receipt processing: new={}, updated={}, total={}",
                newCount, updateCount, main.getReceipts().size());

        // 7. Save MAIN
        try {
            log.info("Saving main entity...");
            TenantOmzet saved = mainRepo.save(main);
            mainRepo.flush();  // ✅ Force flush untuk debug
            log.info("Main entity saved: tenantId={}, receipts={}",
                    saved.getTenantId(), saved.getReceipts().size());
        } catch (Exception e) {
            log.error("ERROR saving main entity: ", e);
            throw e;
        }

        // 8. Update TMP status
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
