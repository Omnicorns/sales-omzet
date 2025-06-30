package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenRequest {
    private String accessToken;
}
