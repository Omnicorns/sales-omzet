package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.processor;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.entity.Token;
import com.sarinah.tenantsalesomzet.repository.TokenRepository;
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
    private final TokenRepository tokenRepository;



    public TokenSecurityValidator(PostGetTokenService postGetTokenService, TokenRepository tokenRepository) {
        this.postGetTokenService = postGetTokenService;
        this.tokenRepository = tokenRepository;
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
            validateExpToken(postGetTokenResponse.getAccessTokenExpiryTime(),postGetTokenResponse.getAccessToken(),postGetTokenResponse.getIsUsedToken());



    }

    private void validateExpToken(Timestamp tokenExpTime, String accessToken,Boolean isUsedToken){
//        Token token = tokenRepository
//                .findByAccessToken(accessToken)
//                .orElseThrow(() ->
//                        new BusinessException(
//                                ERROR_CODE_30000,
//                                ERROR_MESSAGE_INVALID_ACCESS_TOKEN
//                        )
//                );

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 2. Jika pertama kali dipakai (isUsedToken == false)
//        if (!Boolean.TRUE.equals(isUsedToken)) {
//            // 2a. Set expiry = now + 10 menit
//            Timestamp firstExpiry = new Timestamp(now.getTime() + 10L * 60 * 1000);
//            token.setAccessTokenExpiryTime(firstExpiry);
//            // 2b. Tandai sudah dipakai
//            token.setIsUsedToken(true);
//            // 2c. Simpan perubahan
//            tokenRepository.save(token);
//
//            log.info("First use of token, set expiry to {}", firstExpiry);
//            return;  // selesai, tidak lanjut ke validasi expired
//        }


        Timestamp currentTime = new Timestamp(new Date().getTime());
        boolean expired = currentTime.after(tokenExpTime);


      //  try {
            if (expired) {
                log.error("Access Token Expired at {}", tokenExpTime);
                throw new BusinessException(ERROR_CODE_30000, ERROR_MESSAGE_EXPIRED_ACCESS_TOKEN);
            }
            // Kalau belum expired, method akan selesai normal
     //   } finally {
       //     if (expired) {
                // Perpanjang 10 menit dari waktu sekarang
      //          Timestamp newExpiry = new Timestamp(currentTime.getTime() + 10L * 60 * 1000);
       //         tokenRepository.updateExpiryTimeByAccessToken(accessToken, newExpiry);
      //          log.info("Extended token expiry to {}", newExpiry);
      //      }newExpiry
        //  }

    }
}
