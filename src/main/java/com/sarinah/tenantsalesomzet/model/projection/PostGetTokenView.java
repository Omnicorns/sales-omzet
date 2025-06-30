package com.sarinah.tenantsalesomzet.model.projection;

import java.sql.Timestamp;

public interface PostGetTokenView {
    String getAccessToken();
    Timestamp getAccessTokenExpiryTime();
    String getScopes();
    String getAppId();
}
