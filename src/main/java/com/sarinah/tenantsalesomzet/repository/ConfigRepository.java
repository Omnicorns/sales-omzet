package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigRepository extends JpaRepository<Config, String> {
    List<Config> findByParameterKeyInAndIsDeletedFalse(List<String> parameterKey);
}
