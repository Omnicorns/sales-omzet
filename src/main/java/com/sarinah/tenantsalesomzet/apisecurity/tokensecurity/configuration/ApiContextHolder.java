package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.model.ApiContext;

public class ApiContextHolder {
    private static final ThreadLocal<ApiContext> CONTEXT = new ThreadLocal<>();
    public static void setContext(ApiContext ctx) {
        CONTEXT.set(ctx);
    }

    public static ApiContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
