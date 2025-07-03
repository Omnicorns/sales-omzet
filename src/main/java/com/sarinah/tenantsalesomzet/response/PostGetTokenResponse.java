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
public class PostGetTokenResponse {
    private String userId;
    private String cif;
    private String[] scopes;
    private String appId;
    private String accessToken;
    private Timestamp accessTokenExpiryTime;
    private Boolean isUsedToken;
}
