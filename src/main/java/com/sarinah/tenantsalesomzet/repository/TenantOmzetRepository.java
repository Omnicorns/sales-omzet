package com.sarinah.tenantsalesomzet.repository;

import com.sarinah.tenantsalesomzet.model.entity.TenantOmzet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Date;
import java.util.Optional;


public interface TenantOmzetRepository extends JpaRepository<TenantOmzet,String> {
    Page<TenantOmzet> findBySalesDateGreaterThanEqualAndSalesDateLessThan(Date startDate, Date endDate, Pageable pageable);
    Optional<TenantOmzet> findByTenantNameAndSalesDate(String name, Date salesDate);
    Page<TenantOmzet> findByUpdatedTimeBetween(Date start, Date end, Pageable pageable);


}
