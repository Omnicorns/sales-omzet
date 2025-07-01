package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.CustomException;
import com.sarinah.tenantsalesomzet.repository.ClientRepository;
import com.sarinah.tenantsalesomzet.repository.TokenRepository;
import com.sarinah.tenantsalesomzet.request.PostRevokeTokenRequest;
import com.sarinah.tenantsalesomzet.response.PostRevokeTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static com.sarinah.tenantsalesomzet.util.Constant.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class PostRevokeTokenService {
    private final TokenRepository tokenRepository;
    private final ClientRepository clientRepository;

    public PostRevokeTokenResponse execute(PostRevokeTokenRequest request) {
        var client = clientRepository.findClientByAuthClientId(request.getAuthClientId());
        if (client.isEmpty()) {
            throw new CustomException(ERR_INVALID_AUTH_CLIENT, ERR_MSG_INVALID_AUTH_CLIENT);
        }

        if (client.get().isDeleted()) {
            throw new CustomException(ERR_INVALID_AUTH_CLIENT_STATUS, ERR_MSG_INVALID_AUTH_CLIENT_STATUS);
        }

        tokenRepository.updateIsUsedTokenAndIsDeleted(request.getAppId(), request.getAccessToken());
        return new PostRevokeTokenResponse();
    }
}
