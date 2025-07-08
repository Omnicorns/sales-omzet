package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.model;

public class ApiContext {
    private final String tenantName;
    private final String tenantBrand;

    public ApiContext(String tenantName, String tenantBrand) {
        this.tenantName  = tenantName;
        this.tenantBrand = tenantBrand;
    }

    public String getTenantName()  { return tenantName; }
    public String getTenantBrand() { return tenantBrand; }
}
