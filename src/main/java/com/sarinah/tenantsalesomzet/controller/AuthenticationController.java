package com.sarinah.tenantsalesomzet.controller;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation.TokenScope;
import com.sarinah.tenantsalesomzet.model.entity.Login;
import com.sarinah.tenantsalesomzet.request.*;
import com.sarinah.tenantsalesomzet.response.*;
import com.sarinah.tenantsalesomzet.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final PostLoginService postLoginService;
    private final CreateLoginService createLoginService;

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

    @PostMapping(value="/login")
    public PostLoginResponse postLoginResponse (@RequestBody LoginRequest loginRequest){
        return  postLoginService.execute(loginRequest);
    }

    @PostMapping(value = "/api/login")
    public ResponseEntity<?> createLogin( @RequestBody CreateLoginRequest request) {
        Login savedLogin = createLoginService.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateLoginResponse.builder()
                        .userId(savedLogin.getUserId())
                        .username(savedLogin.getUsername())
                        .tenantName(savedLogin.getTenantName())
                        .tenantBrand(savedLogin.getTenantBrand())
                        .message("User berhasil dibuat")
                        .build());
    }

}
