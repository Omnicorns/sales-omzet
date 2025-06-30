package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration.TokenSecurityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({TokenSecurityConfiguration.class})
public @interface EnableTokenSecurity {



}

