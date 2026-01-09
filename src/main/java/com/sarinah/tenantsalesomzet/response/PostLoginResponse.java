package com.sarinah.tenantsalesomzet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginResponse {
    private String username;
    private String tenantBrand;
    private String userId;
    private String tenantNama;
}
