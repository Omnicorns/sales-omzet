package com.sarinah.tenantsalesomzet.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SimpleAppIdUtil {

    public static String encodeAppId(String tenantName, String tenantBrand) {
        String input = tenantName + "::" + tenantBrand;
        return Base64.getEncoder()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode Base64 AppId kembali ke array { tenantName, tenantBrand }.
     */
    public static String[] decodeAppId(String appId) {
        byte[] decoded = Base64.getDecoder().decode(appId);
        String joint = new String(decoded, StandardCharsets.UTF_8);
        return joint.split("::", 2);  // [0]=tenantName, [1]=tenantBrand
    }
}
