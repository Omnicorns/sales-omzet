package com.sarinah.tenantsalesomzet.controller;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation.TokenScope;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.request.TenantOmzetDateRangeRequest;
import com.sarinah.tenantsalesomzet.response.TenantOmzetResponse;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import com.sarinah.tenantsalesomzet.service.GetTenantSalesOmzetService;
import com.sarinah.tenantsalesomzet.service.PostTenantOmzetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tenant-sales/v1")
public class OmzetController {

    private final PostTenantOmzetService postTenantOmzetService;
    private final GetTenantSalesOmzetService getTenantSalesOmzetService;

    @PostMapping(value = "/omzet")
    @TokenScope
    public ValidationResponse post(@RequestBody PostTenantOmzetRequest postAuthCodeRequest) {
        return postTenantOmzetService.execute(postAuthCodeRequest);
    }

    @PostMapping("/omzet-counts")
    public ResponseEntity<Page<TenantOmzetResponse>> getPagedOmzet(
            @RequestBody TenantOmzetDateRangeRequest request) {

        int page = request.getPage();
        int size = request.getSize();

        Pageable pageable;
        String dir = request.getSortDirection(); // bisa null, "", "ASC", atau "DESC"

        if (dir != null && !dir.isBlank()) {
            // 1. Kalau ada nilai, coba parse
            Sort.Direction direction;
            try {
                direction = Sort.Direction.fromString(dir);
            } catch (IllegalArgumentException ex) {
                // nilai bukan ASC/DESC → fallback ke DESC (atau bisa lempar 400)
                direction = Sort.Direction.DESC;
            }
            // 2. Apply sort by updatedTime
            Sort sort = Sort.by(direction, "updatedTime");
            pageable = PageRequest.of(page, size, sort);

        } else {
            // 3. Kalau tidak ada sortDirection, build unsorted pageable
            pageable = PageRequest.of(page, size);
        }

        Page<TenantOmzetResponse> result = getTenantSalesOmzetService
                .execute(request.getStartDate(), request.getEndDate(), request.getIsOnlyTodayUpdate(), pageable);

        return ResponseEntity.ok(result);
    }
}
