package com.sarinah.tenantsalesomzet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostApplyTokenResponse {
    private String accessToken;
    private Timestamp accessTokenExpiryTime;
    private String refreshToken;
    private Timestamp refreshTokenExpiryTime;
    private String extendInfo;
}
