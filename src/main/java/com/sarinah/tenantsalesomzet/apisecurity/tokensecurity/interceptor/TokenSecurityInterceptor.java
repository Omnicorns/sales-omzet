package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.processor.TokenSecurityValidator;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.sarinah.tenantsalesomzet.util.JsonToStringConverter.convertJsonToString;

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

        String requestBody = null;
        try {
            requestBody = convertJsonToString(joinPoint.getArgs());
        } catch (JsonProcessingException e) {
            requestBody = "{}";
        }

        JSONArray jsonArray = new JSONArray(requestBody);

        JSONObject jsonObject = jsonArray.getJSONObject(0);

        tokenSecurityValidator.validateToken(jsonObject.getString("accessToken"));

    }
}
