package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfigRepository extends JpaRepository<Config, String> {

    Optional<Config> findOneByParameterKeyAndIsDeletedFalse(String parameterKey);


    List<Config> findByParameterKeyInAndIsDeletedFalse(List<String> parameterKey);
}
