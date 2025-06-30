package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.projection.PostGetTokenView;
import com.sarinah.tenantsalesomzet.repository.TokenRepository;
import com.sarinah.tenantsalesomzet.request.PostGetTokenRequest;
import com.sarinah.tenantsalesomzet.response.PostGetTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sarinah.tenantsalesomzet.util.Constant.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class PostGetTokenService {
    private final TokenRepository tokenRepository;

    public PostGetTokenResponse execute(PostGetTokenRequest request) {
        Optional<PostGetTokenView> accessTokenOptional = tokenRepository.getTokenByAccessToken(request.getAccessToken());

        if (accessTokenOptional.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000, ERROR_MESSAGE_INVALID_ACCESS_TOKEN);
        }

        PostGetTokenView tokenView = accessTokenOptional.get();

        return PostGetTokenResponse.builder()
                .scopes(tokenView.getScopes().split(","))
                .appId(tokenView.getAppId())
                .accessToken(tokenView.getAccessToken())
                .accessTokenExpiryTime(tokenView.getAccessTokenExpiryTime())
                .build();
    }

}
