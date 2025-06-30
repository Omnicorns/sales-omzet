package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantOmzetReceiptRepository extends JpaRepository<TenantOmzetReceipt,String> {
    Optional<TenantOmzetReceipt> findByReceiptNumber(String receiptNumber);

}
