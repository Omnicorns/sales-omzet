package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.processor;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.request.PostGetTokenRequest;
import com.sarinah.tenantsalesomzet.response.PostGetTokenResponse;
import com.sarinah.tenantsalesomzet.service.PostGetTokenService;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.Date;

import static com.sarinah.tenantsalesomzet.util.Constant.*;

@Log4j2
public class TokenSecurityValidator {
   private final  PostGetTokenService postGetTokenService;


    public TokenSecurityValidator(PostGetTokenService postGetTokenService) {
        this.postGetTokenService = postGetTokenService;
    }

    public void validateToken(String reqToken) {

        if(null == reqToken){

            throw new BusinessException(ERROR_CODE_30000, ERROR_MESSAGE_INVALID_ACCESS_TOKEN);
        }


        PostGetTokenResponse postGetTokenResponse = postGetTokenService.execute(PostGetTokenRequest.builder().accessToken(reqToken).build());
            if (null == postGetTokenResponse.getAppId()){
                log.error(" Access Token Not Valid");
                throw new BusinessException(ERROR_CODE_30000, ERROR_MESSAGE_INVALID_ACCESS_TOKEN);
            }
            validateExpToken(postGetTokenResponse.getAccessTokenExpiryTime());


    }

    private void validateExpToken(Timestamp tokenExpTime){

        Timestamp currentTime = new Timestamp(new Date().getTime());

        if (currentTime.after(tokenExpTime)) {
            log.error(" Access Token Expired");
            throw new BusinessException(ERROR_CODE_30000, ERROR_MESSAGE_EXPIRED_ACCESS_TOKEN);
        }

    }
}
