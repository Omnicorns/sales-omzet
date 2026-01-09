package com.sarinah.tenantsalesomzet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoginRequest {

    private String userId;

    private String username;

    private String tenantName;
    private String tenantBrand;


    private String password;
}
