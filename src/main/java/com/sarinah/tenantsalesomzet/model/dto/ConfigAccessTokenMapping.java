package com.sarinah.tenantsalesomzet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigAccessTokenMapping {
    private String accessTokenExpiryTime;
    private String refreshTokenExpiryTime;
    private String authCodeExpiryTime;
    private String grantType;
}
