package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.entity.AuthCode;
import com.sarinah.tenantsalesomzet.repository.AuthCodeRepository;
import com.sarinah.tenantsalesomzet.request.PostAuthCodeRequest;
import com.sarinah.tenantsalesomzet.response.PostAuthCodeResponse;

import com.sarinah.tenantsalesomzet.util.SimpleAppIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Optional;
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
            String tenantName  = postAuthCodeRequest.getTenantName();
            String tenantBrand = postAuthCodeRequest.getTenantBrand();

            String appId = SimpleAppIdUtil.encodeAppId(tenantName, tenantBrand);

            // 2. auth code deterministic berdasarkan tenantName::tenantBrand
            String authorizedCode = generateAuthCode(tenantName, tenantBrand);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp expiry = new Timestamp(
                    now.getTime() + TimeUnit.SECONDS.toMillis(Long.parseLong(postAuthCodeRequest.getExpirySeconds()))
            );

            Optional<AuthCode> existingOpt =
                    authCodeRepository.findByAuthorizedCodeAndIsDeletedFalse(authorizedCode);

            if (existingOpt.isPresent()) {
                AuthCode existing = existingOpt.get();

                // reuse jika belum dipakai dan belum expired
                if (!existing.getIsUsedToken() && existing.getAuthExpiryTime().after(now)) {
                    return PostAuthCodeResponse.builder()
                            .authCode(authorizedCode)
                            .build();
                }

                // otherwise reset record
                existing.setAuthExpiryTime(expiry);
                existing.setIsUsedToken(false);
                existing.setUpdatedTime(now);
                existing.setUpdatedBy("SYSTEM");
                authCodeRepository.save(existing);

                return PostAuthCodeResponse.builder()
                        .authCode(authorizedCode)
                        .build();
            }

            // 4. buat record baru
            AuthCode authCode = AuthCode.builder()
                    .authCodeId(UUID.randomUUID().toString())
                    .appId(appId)
                    .scopes(String.join(",",postAuthCodeRequest.getScopes()))
                    .authorizedCode(authorizedCode)
                    .authExpiryTime(expiry)
                    .isUsedToken(false)
                    .isDeleted(false)
                    .createdTime(now)
                    .createdBy("SYSTEM")
                    .updatedTime(now)
                    .updatedBy("SYSTEM")
                    .build();

            authCodeRepository.save(authCode);

            return PostAuthCodeResponse.builder()
                    .authCode(authorizedCode)
                    .build();

        } catch (Exception e) {
            log.error("Err: {}", e.getMessage());
            throw new BusinessException(ERR_CODE_80000, "Unable to generate authCode");
        }

    }

    private void validationAuthCode(PostAuthCodeRequest postAuthCodeRequest) {
        if (StringUtils.isBlank(postAuthCodeRequest.getTenantName()) || ArrayUtils.isEmpty(postAuthCodeRequest.getScopes())) {
            log.error("Invalid request, all parameter is blank");
            throw new BusinessException(BAD_REQUEST, ERROR_CODE_30000, "invalid request, all parameter is blank");
        }
    }




    public static String generateAuthCode(String tenantName, String tenantBrand) {
        String input = tenantName + "::" + tenantBrand;
        return UUID.nameUUIDFromBytes(input.getBytes(StandardCharsets.UTF_8))
                .toString();
    }
}
