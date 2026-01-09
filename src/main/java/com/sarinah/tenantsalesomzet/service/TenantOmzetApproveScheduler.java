package com.sarinah.tenantsalesomzet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantOmzetApproveScheduler {
    private final ApproveTenantOmzetBatchService batchService;

    // === CONFIG TANPA YML ===
    private static final boolean ENABLED = true;
    private static final String  CRON_EXPR = "0 */1 * * * *"; // tiap 5 menit (Spring cron 6 field)
    private static final int     BATCH_SIZE = 200;

    @Scheduled(cron = CRON_EXPR, zone = "Asia/Jakarta")
    public void autoApprove() {
        if (!ENABLED) return;

        batchService.approvePendingBatch(BATCH_SIZE, "CRON");
    }
}
