package com.sarinah.tenantsalesomzet.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"receipts"})  // ✅ Exclude collection
@EqualsAndHashCode(exclude = {"receipts"})  // ✅ Exclude dari equals/hashCode juga
@Entity
@Table(name = "tenant_omzet")
public class TenantOmzet {
    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "sales_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date salesDate;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "day")
    private String day;

    @Column(name = "lot_location")
    private String lotLocation;

    @Column(name="omzet", precision = 15, scale = 2)
    private BigDecimal omzet;

    @Column(name = "created_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdTime;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedTime;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "tenantOmzet",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TenantOmzetReceipt> receipts = new ArrayList<>();
}
