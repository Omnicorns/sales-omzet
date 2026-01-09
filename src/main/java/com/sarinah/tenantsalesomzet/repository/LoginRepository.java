package com.sarinah.tenantsalesomzet.repository;


import com.sarinah.tenantsalesomzet.model.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository  extends JpaRepository<Login, String> {
    Optional<Login> findByUsername(String username);
    boolean existsByUsername(String username);
}
