package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetTmp;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetTmpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApproveTenantOmzetBatchService {
    private final TenantOmzetTmpRepository tmpRepo;
    private final ApproveTenantOmzetService approveService;

    /**
     * Approve N data PENDING. Aman untuk dipanggil berkali-kali.
     */
    public int approvePendingBatch(int limit, String approvedBy) {
        Page<TenantOmzetTmp> page = tmpRepo.findByApprovalStatusOrderBySubmittedTimeAsc(
                TenantOmzetTmp.ApprovalStatus.PENDING, PageRequest.of(0, limit)
        );

        int success = 0;
        for (TenantOmzetTmp t : page.getContent()) {
            try {
                approveService.approveOne(t.getTenantId(), approvedBy);
                success++;
            } catch (Exception e) {
                // pilih strategi:
                // 1) skip dan lanjut (umum untuk batch)
                // 2) log error (disarankan)
            }
        }
        return success;
    }

}
