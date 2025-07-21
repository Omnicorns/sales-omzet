package com.sarinah.tenantsalesomzet.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantOmzetDateRangeRequest {
    private String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date endDate;
    @Builder.Default
    private int page = 0;  // default halaman pertama

    @Builder.Default
    private int size = 20;// default 20 data per halaman

    private String sortDirection;

}
