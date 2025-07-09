package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.interceptor;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.processor.TokenSecurityValidator;
import com.sarinah.tenantsalesomzet.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;
import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_MESSAGE_INVALID_ACCESS_TOKEN;

@Log4j2
@Aspect
public class TokenSecurityInterceptor {
    private TokenSecurityValidator tokenSecurityValidator;

    public TokenSecurityInterceptor(TokenSecurityValidator tokenSecurityValidator) {
        this.tokenSecurityValidator = tokenSecurityValidator;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    private void controller() {
    }

    @Pointcut("@annotation(com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation.TokenScope)")
    private void tokenScope() {
    }

    @Before("controller() && tokenScope()")
    public void validate(JoinPoint joinPoint) {

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attrs.getRequest();

        // Baca header Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(
                    ERROR_CODE_30000, ERROR_MESSAGE_INVALID_ACCESS_TOKEN);
        }

        String token = authHeader.substring(7);
        tokenSecurityValidator.validateToken(token);

    }
}
