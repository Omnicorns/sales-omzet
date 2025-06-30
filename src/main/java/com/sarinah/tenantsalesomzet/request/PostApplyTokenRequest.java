package com.sarinah.tenantsalesomzet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostApplyTokenRequest {
    private String appId;
    private String authClientId;
    private String grantType;
    private String authCode;
    private String refreshToken;

}
