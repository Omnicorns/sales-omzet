package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.interceptor.TokenSecurityInterceptor;
import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.processor.TokenSecurityValidator;
import com.sarinah.tenantsalesomzet.repository.TokenRepository;
import com.sarinah.tenantsalesomzet.service.PostGetTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenSecurityConfiguration {




    @Bean
    public TokenSecurityInterceptor tokenSecurityValidationInterceptor(TokenSecurityValidator tokenSecurityValidator) {
        return new TokenSecurityInterceptor(tokenSecurityValidator);
    }


    @Bean
    public TokenSecurityValidator tokenSecurityValidator(
            PostGetTokenService postGetTokenService,
            TokenRepository tokenRepository
    ) {
        return new TokenSecurityValidator(postGetTokenService,tokenRepository);
    }
}
