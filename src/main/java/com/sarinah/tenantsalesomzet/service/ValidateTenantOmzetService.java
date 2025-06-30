package com.sarinah.tenantsalesomzet.service;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.request.PostTenantOmzetRequest;
import com.sarinah.tenantsalesomzet.response.ValidationResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;


import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ValidateTenantOmzetService {
    public ValidationResponse execute (PostTenantOmzetRequest input){
        this.doFilterBrandName(input.getBrandName());
        this.doFilterOmzet(input.getOmzet());
        return ValidationResponse.builder().result(true).build();
    }

    private void doFilterBrandName(String input) {
        if (StringUtils.isEmpty(input)) {
            throw new BusinessException(BAD_REQUEST, ERROR_CODE_30000, "invalid request, all parameter is blank");

        }
    }

    private void doFilterOmzet(BigDecimal input){
        if (ObjectUtils.isEmpty(input)){
            throw new BusinessException(BAD_REQUEST, ERROR_CODE_30000, "invalid request, all parameter is blank");
    }
    }

}