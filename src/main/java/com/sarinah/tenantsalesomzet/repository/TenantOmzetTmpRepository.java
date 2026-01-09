package com.sarinah.tenantsalesomzet.repository;


import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetReceiptTmp;
import com.sarinah.tenantsalesomzet.model.entity.TenantOmzetTmp;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TenantOmzetTmpRepository extends JpaRepository<TenantOmzetTmp,String> {
    Page<TenantOmzetTmp> findBySalesDateGreaterThanEqualAndSalesDateLessThan(Date startDate, Date endDate, Pageable pageable);
    Optional<TenantOmzetTmp> findByTenantNameAndSalesDate(String name, Date salesDate);
    Page<TenantOmzetTmp> findByUpdatedTimeBetween(Date start, Date end, Pageable pageable);
    Page<TenantOmzetTmp> findBySalesDateGreaterThanEqualAndSalesDateLessThanAndBrandName(
            Date startDate, Date endDate, String brandName, Pageable pageable);
    Page<TenantOmzetTmp> findByUpdatedTimeBetweenAndBrandName(
            Date start, Date end, String brandName, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TenantOmzetTmp t where t.id = :id")
    Optional<TenantOmzetTmp> findByIdForUpdate(@Param("id") String id);

    List<TenantOmzetTmp> findByApprovalStatus(TenantOmzetTmp.ApprovalStatus status);

    Page<TenantOmzetTmp> findByApprovalStatusOrderBySubmittedTimeAsc(
            TenantOmzetTmp.ApprovalStatus status, Pageable pageable
    );
}
