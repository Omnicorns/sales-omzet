package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.model.entity.Login;
import com.sarinah.tenantsalesomzet.repository.LoginRepository;
import com.sarinah.tenantsalesomzet.request.CreateLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Log4j2
@RequiredArgsConstructor
@Service
public class CreateLoginService {
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Login execute(CreateLoginRequest request) {
        // Validasi userId belum ada
        if (loginRepository.existsById(request.getUserId())) {
            throw new BusinessException("", "User ID sudah terdaftar");
        }

        // Validasi username belum dipakai
        if (loginRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("", "Username sudah digunakan");
        }

        // Build entity
        Login login = Login.builder()
                .userId(request.getUserId())
                .username(request.getUsername())
                .tenantName(request.getTenantName())
                .tenantBrand(request.getTenantBrand())
                .password(passwordEncoder.encode(request.getPassword())) // enkripsi password
                .isDeleted(false) // default false
                .build();

        // Save ke database
        return loginRepository.save(login);
    }

}
