package com.sarinah.tenantsalesomzet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthCodeRequest {
    private String tenantName;
    private String tenantBrand;
    private String[] scopes;
    private String expirySeconds;
}
