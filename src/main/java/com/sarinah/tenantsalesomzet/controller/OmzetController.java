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
import org.springframework.http.ResponseEntity;
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

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<TenantOmzetResponse> result = getTenantSalesOmzetService
                .execute(request.getStartDate(), request.getEndDate(), pageable);

        return ResponseEntity.ok(result);
    }
}
