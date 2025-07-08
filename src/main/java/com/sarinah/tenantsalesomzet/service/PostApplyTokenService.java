package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.exception.CustomException;
import com.sarinah.tenantsalesomzet.model.dto.ConfigAccessTokenMapping;
import com.sarinah.tenantsalesomzet.model.entity.Config;
import com.sarinah.tenantsalesomzet.model.entity.Token;
import com.sarinah.tenantsalesomzet.repository.AuthCodeRepository;
import com.sarinah.tenantsalesomzet.repository.ClientRepository;
import com.sarinah.tenantsalesomzet.repository.ConfigRepository;
import com.sarinah.tenantsalesomzet.repository.TokenRepository;
import com.sarinah.tenantsalesomzet.request.PostApplyTokenRequest;
import com.sarinah.tenantsalesomzet.request.TokenRequest;
import com.sarinah.tenantsalesomzet.response.PostApplyTokenResponse;
import com.sarinah.tenantsalesomzet.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.sarinah.tenantsalesomzet.util.Constant.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class PostApplyTokenService {
    private final AuthCodeRepository authCodeRepository;
    private final TokenRepository tokenRepository;
    private final ClientRepository clientRepository;
    private final ConfigRepository configRepository;

    public PostApplyTokenResponse execute(PostApplyTokenRequest request) {
        ConfigAccessTokenMapping config = getConfigs();

        validateGrantType(request, config);

        //validate authClientId
        validateAuthClientID(request);
        var tokenRequest = new TokenRequest();


        if (request.getGrantType().equals(GRANT_TYPE_AUTHORIZATION_CODE)) {

            //validate authCode
            validateAuthCode(request.getAuthCode(), tokenRequest);

        } else if (request.getGrantType().equals(GRANT_TYPE_REFRESH_TOKEN)) {

            //validate refresh token
            validateRefreshToken(request.getRefreshToken(), tokenRequest);

        } else {
            throw new BusinessException(ERROR_CODE_30000,ERR_MSG_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE);
        }

        Token token = generateToken(request, config.getAccessTokenExpiryTime(), config.getRefreshTokenExpiryTime(), tokenRequest);

        return PostApplyTokenResponse.builder()
                .accessToken(token.getAccessToken())
                .accessTokenExpiryTime(token.getAccessTokenExpiryTime())
                .refreshToken(token.getRefreshToken())
                .refreshTokenExpiryTime(token.getRefreshTokenExpiryTime())
                .build();

    }

    private void validateGrantType(PostApplyTokenRequest request, ConfigAccessTokenMapping config) {
        String[] arrStr = config.getGrantType().split(" | ");
        boolean check = false;
        for (String data : arrStr) {
            if (data.equals(request.getGrantType())) {
                check = true;
                break;
            }
        }
        if (!check) {
           throw new BusinessException(ERROR_CODE_30000, ERR_MSG_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE);
        }


    }
    private void validateAuthClientID(PostApplyTokenRequest request) {
        var data = clientRepository.getExistAuthClient(request.getAuthClientId());
        if (data.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000, ERR_MSG_INVALID_AUTH_CLIENT);
        }

        if (data.get().getIsDeleted() == 1) {
            throw new BusinessException(ERROR_CODE_30000, ERR_MSG_INVALID_AUTH_CLIENT_STATUS);
        }
    }

    private void validateAuthCode(String authCodeToken, TokenRequest tokenRequest) {
        var authCodeOptional = authCodeRepository.findByAuthCode(authCodeToken);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (authCodeOptional.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000, ERR_MSG_INVALID_AUTHORIZATION_CODE);
        } else {
            var authCode = authCodeOptional.get();
            if (currentTime.after(authCode.getAuthExpiryTime())) {
                throw new BusinessException(ERROR_CODE_30000, ERR_MSG_EXPIRED_AUTHORIZATION_CODE);
            } else if (authCode.getIsUsedToken()) {
                throw new BusinessException(ERROR_CODE_30000, ERR_MSG_USED_AUTHORIZATION_CODE);
            }
            authCode.setIsUsedToken(true);
            authCodeRepository.save(authCode);

            tokenRequest.setUserId(authCode.getAppId());
            tokenRequest.setScopes(authCode.getScopes());

        }
    }
    private void validateRefreshToken(String refreshToken, TokenRequest tokenRequest) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        Optional<Token> refreshTokenOptional = tokenRepository.findByRefreshToken(refreshToken);

        if (refreshTokenOptional.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000, ERR_MSG_INVALID_REFRESH_TOKEN);
        }

        var refToken = refreshTokenOptional.get();
        if (currentTime.after(refToken.getRefreshTokenExpiryTime())) {
            throw new BusinessException(ERROR_CODE_30000, ERR_MSG_EXPIRED_REFRESH_TOKEN);
        } else if (refToken.getIsUsedToken()) {
            throw new BusinessException(ERROR_CODE_30000,ERR_MSG_USED_REFRESH_TOKEN);
        }

        refToken.setIsUsedToken(true);
        tokenRepository.save(refToken);

        tokenRequest.setUserId(refToken.getRefreshToken());
        tokenRequest.setScopes(refToken.getScopes());
    }

    private Token generateToken(PostApplyTokenRequest request, String accessTokenExp, String refreshTokenExp, TokenRequest tokenRequest) {
            String appId        = tokenRequest.getUserId();
            String authClientId = request.getAuthClientId();
            Timestamp now       = new Timestamp(System.currentTimeMillis());

            // Cek token existing untuk appId + authClientId
            Optional<Token> existingOpt =
                    tokenRepository.findByAppIdAndAuthClientIdAndIsDeletedFalse(appId, authClientId);

            if (existingOpt.isPresent()) {
                Token existing = existingOpt.get();
                if (existing.getAccessTokenExpiryTime().after(now)) {
                    // reuse token yang masih valid
                    return existing;
                }
                // tandai expired token sebagai deleted
                existing.setIsDeleted(true);
                existing.setUpdatedTime(now);
                existing.setUpdatedBy("SYSTEM");
                tokenRepository.save(existing);
            }

            // generate token baru
            String newAccessToken  = UUID.randomUUID().toString();
            String newRefreshToken = UUID.randomUUID().toString();

            Token token = Token.builder()
                    .tokenId(UUID.randomUUID().toString())
                    .scopes(tokenRequest.getScopes())
                    .appId(appId)
                    .authClientId(authClientId)
                    .accessToken(newAccessToken)
                    .accessTokenExpiryTime(new Timestamp(
                            now.getTime() + TimeUnit.SECONDS.toMillis(Long.parseLong(accessTokenExp))
                    ))
                    .refreshToken(newRefreshToken)
                    .refreshTokenExpiryTime(new Timestamp(
                            now.getTime() + TimeUnit.SECONDS.toMillis(Long.parseLong(refreshTokenExp))
                    ))
                    .isUsedToken(false)
                    .isDeleted(false)
                    .createdTime(now)
                    .createdBy("SYSTEM")
                    .updatedTime(now)
                    .updatedBy("SYSTEM")
                    .build();

            return tokenRepository.save(token);

    }

    private ConfigAccessTokenMapping getConfigs() {
        ConfigAccessTokenMapping configAccessTokenMapping = new ConfigAccessTokenMapping();
        List<String> configKeyList = List.of(AUTHCODE_EXPIRY_TIME, ACCESSTOKEN_EXPIRY_TIME, REFRESHTOKEN_EXPIRY_TIME, GRANT_TYPE);
        List<Config> configs = configRepository.findByParameterKeyInAndIsDeletedFalse(configKeyList);

        if (configs.isEmpty()) {
            throw new CustomException(Constant.ERR_CODE_80000, CONFIG_IS_EMPTY);
        }

        Map<String, String> configMap;
        configMap = configs.stream().collect(Collectors.toMap(Config::getParameterKey, Config::getParameterValue));
        checkConfigParam(configKeyList, configMap);
        configAccessTokenMapping.setAccessTokenExpiryTime(configMap.get(ACCESSTOKEN_EXPIRY_TIME));
        configAccessTokenMapping.setRefreshTokenExpiryTime(configMap.get(REFRESHTOKEN_EXPIRY_TIME));
        configAccessTokenMapping.setAuthCodeExpiryTime(configMap.get(AUTHCODE_EXPIRY_TIME));
        configAccessTokenMapping.setGrantType(configMap.get(GRANT_TYPE));

        return configAccessTokenMapping;
    }

    private void checkConfigParam(List<String> configKeyList, Map<String, String> configMap) {
        for (String key : configKeyList) {
            if (configMap.get(key) == null) {
                throw new CustomException(ERR_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE, ERR_MSG_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE);
            }
        }
    }

   }
