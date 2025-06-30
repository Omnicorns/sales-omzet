package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.entity.AuthCode;
import com.sarinah.tenantsalesomzet.repository.AuthCodeRepository;
import com.sarinah.tenantsalesomzet.request.PostAuthCodeRequest;
import com.sarinah.tenantsalesomzet.response.PostAuthCodeResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;
import static com.sarinah.tenantsalesomzet.util.Constant.ERR_CODE_80000;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2
@RequiredArgsConstructor
@Service
public class PostAuthCodeService {
    private final AuthCodeRepository authCodeRepository;

    public PostAuthCodeResponse execute(PostAuthCodeRequest postAuthCodeRequest) {
        this.validationAuthCode(postAuthCodeRequest);
        try {
            var authCode = AuthCode.builder()
                    .authCodeId(String.valueOf(UUID.randomUUID()))
                    .appId(postAuthCodeRequest.getAppId())
                    .scopes(String.join(",", postAuthCodeRequest.getScopes()))
                    .authExpiryTime(new Timestamp(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Long.parseLong("300"))))
                    .authorizedCode(UUID.randomUUID().toString())
                    .isUsedToken(false)
                    .isDeleted(false)
                    .build();

            authCode.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            authCode.setCreatedBy("SYSTEM");
            authCode.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            authCode.setUpdatedBy("SYSTEM");

            authCodeRepository.save(authCode);

            return PostAuthCodeResponse.builder()
                    .authCode(authCode.getAuthorizedCode())
                    .build();

        } catch (Exception e) {
            log.error("Err: {}", e.getMessage());
            throw new BusinessException(ERR_CODE_80000, "Unable to generate authCode");
        }

    }

    private void validationAuthCode(PostAuthCodeRequest postAuthCodeRequest) {
        if (StringUtils.isBlank(postAuthCodeRequest.getAppId()) || ArrayUtils.isEmpty(postAuthCodeRequest.getScopes()) || postAuthCodeRequest.getAppId().length() > 32) {
            log.error("Invalid request, all parameter is blank");
            throw new BusinessException(BAD_REQUEST, ERROR_CODE_30000, "invalid request, all parameter is blank");
        }
    }
}
