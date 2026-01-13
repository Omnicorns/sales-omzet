package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetTmp;
import com.sarinah.tenantsalesomzet.repository.TenantOmzetTmpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApproveTenantOmzetBatchService {
    private final TenantOmzetTmpRepository tmpRepo;
    private final ApproveTenantOmzetService approveService;

    /**
     * Approve N data PENDING. Aman untuk dipanggil berkali-kali.
     */
    public int approvePendingBatch(int limit, String approvedBy) {
        Page<TenantOmzetTmp> page = tmpRepo.findNeedApproval(PageRequest.of(0, limit));

        int success = 0;
        for (TenantOmzetTmp t : page.getContent()) {
            try {
                approveService.approveOne(t.getTenantId(), approvedBy); // sesuaikan getter ID kamu
                success++;
            } catch (Exception e) {
                log.error("Approve failed tmpId={}", t.getTenantId(), e);
            }
        }
        return success;
    }

}
