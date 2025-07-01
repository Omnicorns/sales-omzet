package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.Client;
import com.sarinah.tenantsalesomzet.model.projection.AuthClientIdCheckView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    @Query(
            value = "SELECT is_deleted::int AS isDeleted FROM client WHERE auth_client_id = :authClientId",
            nativeQuery = true
    )
    Optional<AuthClientIdCheckView> getExistAuthClient(@Param("authClientId") String authClientId);

    @Query(value = "SELECT * FROM CLIENT c WHERE AUTH_CLIENT_ID LIKE :authClientId", nativeQuery = true)
    Optional<Client> findClientByAuthClientId(@Param("authClientId") String authClientId);


}
