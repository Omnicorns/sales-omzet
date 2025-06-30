package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Date;
import java.util.List;

public interface TenantOmzetRepository extends JpaRepository<TenantOmzet,String> {
    @Query("SELECT t FROM TenantOmzet t LEFT JOIN FETCH t.receipts WHERE t.salesDate BETWEEN :startDate AND :endDate")
    List<TenantOmzet> findBySalesDateBetweenWithReceipts(Date startDate, Date endDate);

    Page<TenantOmzet> findBySalesDateBetween(Date startDate, Date endDate, Pageable pageable);


}
