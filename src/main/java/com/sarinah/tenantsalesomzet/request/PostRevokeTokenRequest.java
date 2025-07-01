package com.sarinah.tenantsalesomzet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRevokeTokenRequest {
    private String accessToken;
    private String appId;
    private String authClientId;
}
