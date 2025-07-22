package com.sarinah.tenantsalesomzet.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sarinah.tenantsalesomzet.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sarinah.tenantsalesomzet.util.Constant.ERROR_CODE_30000;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItem {
    private String receiptNumber;
    private Object amount;
    private Object dpp;
    private Object ppn;
    private Object serviceCharge;
    private String paymentType;
    @JsonIgnore // Agar tidak di-serialize otomatis
    private Date receiptDate;

    // Setter tetap sama, parsing fleksibel string → Date
    @JsonProperty("receiptDate")
    public void setReceiptDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            this.receiptDate = null;
            return;
        }
        String[] patterns = {
                "dd-MM-yyyy HH:mm:ss",
                "dd-MM-yyyy HH:mm",
                "dd-MM-yyyy"
        };
        for (String pattern : patterns) {
            try {
                this.receiptDate = new SimpleDateFormat(pattern).parse(dateStr);
                System.out.println("Parsed date: " + this.receiptDate);
                return;
            } catch (ParseException e) {
                throw new BusinessException(ERROR_CODE_30000,"Format receipt date tidak valid");
            }
        }     throw new BusinessException(ERROR_CODE_30000,"Format receipt date tidak valid");
    }



    // Getter return Date as is (untuk backend logic)
    public Date getReceiptDate() {
        return this.receiptDate;
    }

    // Tambahkan kalau mau custom output ke JSON (opsional, hanya jika butuh String format ke frontend)
    @JsonProperty("receiptDate")
    public String getReceiptDateAsString() {
        if (this.receiptDate == null) return null;
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(this.receiptDate);
    }




    // Setter (optional, Jackson otomatis mapping ke Object)
    @JsonProperty("amount")
    public void setAmount(Object amount) {
        this.amount = amount;
    }

    public Object getAmountRaw() {
        return this.amount;
    }

    // Getter helper: selalu return BigDecimal
    public BigDecimal getAmountAsBigDecimal() {
        if (amount == null) return BigDecimal.ZERO;
        if (amount instanceof Number) {
            return new BigDecimal(amount.toString());
        }
        if (amount instanceof String) {
            String s = ((String) amount).replaceAll("[,.]", "").trim();
            if (!s.matches("^\\d+$")) {
                throw new BusinessException(ERROR_CODE_30000,"Format amount tidak valid");
            }
            if (s.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(s);
        }
        throw new BusinessException(ERROR_CODE_30000,"Format amount tidak valid");
    }


    @JsonProperty("dpp")
    public void setDpp(Object dpp) {
        this.dpp = dpp;
    }

    public Object getDpp() {
        return this.dpp;
    }

    // Getter helper: selalu return BigDecimal
    public BigDecimal getDppAsBigDecimal() {
        if (dpp == null) return BigDecimal.ZERO;
        if (dpp instanceof Number) {
            return new BigDecimal(dpp.toString());
        }
        if (dpp instanceof String) {
            String s = ((String) dpp).replaceAll("[,.]", "").trim();
            if (!s.matches("^\\d+$")) {
                throw new BusinessException(ERROR_CODE_30000,"Format dpp tidak valid");
            }
            if (s.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(s);
        }
        throw new BusinessException(ERROR_CODE_30000,"Format dpp tidak valid");
    }


    @JsonProperty("ppn")
    public void setPpn(Object ppn) {
        this.ppn = ppn;
    }

    public Object getPpn() {
        return this.ppn;
    }

    // Getter helper: selalu return BigDecimal
    public BigDecimal getPpnAsBigDecimal() {
        if (ppn == null) return BigDecimal.ZERO;
        if (ppn instanceof Number) {
            return new BigDecimal(ppn.toString());
        }
        if (ppn instanceof String) {
            String s = ((String) ppn).replaceAll("[,.]", "").trim();
            if (!s.matches("^\\d+$")) {
                throw new BusinessException(ERROR_CODE_30000,"Format ppn tidak valid");
            }
            if (s.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(s);
        }
        throw new BusinessException(ERROR_CODE_30000,"Format ppn tidak valid");
    }

    @JsonProperty("serviceCharge")
    public void setServiceCharge(Object serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public Object getServiceCharge() {
        return this.serviceCharge;
    }

    // Getter helper: selalu return BigDecimal
    public BigDecimal getServiceChargeAsBigDecimal() {
        if (serviceCharge == null) return BigDecimal.ZERO;
        if (serviceCharge instanceof Number) {
            return new BigDecimal(serviceCharge.toString());
        }
        if (serviceCharge instanceof String) {
            String s = ((String) serviceCharge).replaceAll("[,.]", "").trim();
            if (!s.matches("^\\d+$")) {
                throw new BusinessException(ERROR_CODE_30000,"Format serviceCharge tidak valid");
            }
            if (s.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(s);
        }
        throw new BusinessException(ERROR_CODE_30000,"Format serviceCharge tidak valid");
    }

}
