package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.entity.Login;

import com.sarinah.tenantsalesomzet.repository.LoginRepository;
import com.sarinah.tenantsalesomzet.request.LoginRequest;

import com.sarinah.tenantsalesomzet.response.PostLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;


@Log4j2
@RequiredArgsConstructor
@Service
public class PostLoginService {
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    public PostLoginResponse execute(LoginRequest request) {
        Optional<Login> accessTokenOptional = loginRepository.findByUsername(request.getUsername());

        if (accessTokenOptional.isEmpty()) {
            throw new BusinessException(ERROR_CODE_30000,"Username atau password salah" );

        }
        Login tokenView = accessTokenOptional.get();

        if (!passwordEncoder.matches(request.getPassword(),tokenView.getPassword())) {
            throw new BusinessException(ERROR_CODE_30000, "Username atau password salah");
        }




        return PostLoginResponse.builder()
                .username(tokenView.getUsername())
                .tenantBrand(tokenView.getTenantBrand())
                .userId(tokenView.getUserId())
                .tenantNama(tokenView.getTenantName())
                .build();
    }

}
