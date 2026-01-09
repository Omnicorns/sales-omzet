package com.sarinah.tenantsalesomzet.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLoginResponse {
    private String userId;
    private String username;
    private String tenantName;
    private String tenantBrand;
    private String message;
}
