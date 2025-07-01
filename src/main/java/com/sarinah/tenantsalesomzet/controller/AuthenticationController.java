package com.sarinah.tenantsalesomzet.controller;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation.TokenScope;
import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.model.AccessTokenRequest;
import com.sarinah.tenantsalesomzet.request.PostApplyTokenRequest;
import com.sarinah.tenantsalesomzet.request.PostAuthCodeRequest;
import com.sarinah.tenantsalesomzet.request.PostRevokeTokenRequest;
import com.sarinah.tenantsalesomzet.response.PostApplyTokenResponse;
import com.sarinah.tenantsalesomzet.response.PostAuthCodeResponse;
import com.sarinah.tenantsalesomzet.response.PostRevokeTokenResponse;
import com.sarinah.tenantsalesomzet.service.PostApplyTokenService;
import com.sarinah.tenantsalesomzet.service.PostAuthCodeService;
import com.sarinah.tenantsalesomzet.service.PostRevokeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tenant-sales/authentication/v1")
public class AuthenticationController {
    private final PostAuthCodeService postAuthCodeService;
    private final PostApplyTokenService postApplyTokenService;
    private final PostRevokeTokenService postRevokeTokenService;

    @PostMapping(value = "/authCode")
    public PostAuthCodeResponse postAuthCode(@RequestBody PostAuthCodeRequest postAuthCodeRequest) {
        return postAuthCodeService.execute(postAuthCodeRequest);
    }

    @PostMapping(value = "/applyToken")
    public PostApplyTokenResponse postApplyTokenResponse(@RequestBody  PostApplyTokenRequest request) {
        return postApplyTokenService.execute(request);
    }

    @PostMapping(value = "/revokeToken")
    @TokenScope
    public PostRevokeTokenResponse postRevokeTokenService (@RequestBody PostRevokeTokenRequest request){
       return postRevokeTokenService.execute(request);
    }

}
