package com.sarinah.tenantsalesomzet.util;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String ERR_CODE_80000 = "80000";
    public static final String ERROR_CODE_30000 = "30000";
    public static final String ERR_DATA_NOT_FOUND = "DATA_NOT_FOUND";
    public static final String ERR_MSG_DATA_NOT_FOUND = "Data Not Found";
    public static final String ERR_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE = "AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE";
    public static final String ERR_MSG_AUTH_CLIENT_UNSUPPORTED_GRANT_TYPE = "The authorized merchant does not support this grant type.";
    public static final String ERR_INVALID_AUTH_CLIENT = "INVALID_AUTH_CLIENT";
    public static final String ERR_MSG_INVALID_AUTH_CLIENT = "Either the authorized merchant does not exist or the merchant does not onboard to the native app.";
    public static final String ERR_INVALID_AUTH_CLIENT_STATUS="INVALID_AUTH_CLIENT_STATUS";
    public static final String ERR_MSG_INVALID_AUTH_CLIENT_STATUS="The status of the authorized merchant is invalid.";
    public static final String ERR_INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";
    public static final String ERR_MSG_INVALID_REFRESH_TOKEN = "The refresh token does not exist.";
    public static final String ERR_EXPIRED_REFRESH_TOKEN = "EXPIRED_REFRESH_TOKEN";
    public static final String ERR_MSG_EXPIRED_REFRESH_TOKEN = "The refresh token expires.";
    public static final String ERR_USED_REFRESH_TOKEN = "USED_REFRESH_TOKEN";
    public static final String ERR_MSG_USED_REFRESH_TOKEN = "The refresh token has been used.";
    public static final String ERR_INVALID_AUTHCODE = "INVALID_AUTHCODE";
    public static final String ERR_MSG_INVALID_AUTHORIZATION_CODE = "The authorization code does not exist.";
    public static final String ERR_USED_AUTHORIZATION_CODE = "USED_AUTHCODE";
    public static final String ERR_MSG_USED_AUTHORIZATION_CODE = "The authorization code has been used.";
    public static final String ERR_EXPIRED_AUTHORIZATION_CODE = "EXPIRED_AUTHCODE";
    public static final String ERR_MSG_EXPIRED_AUTHORIZATION_CODE = "The authorization code expires.";
    public static final String ERROR_CODE_INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";
    public static final String ERROR_CODE_EXPIRED_ACCESS_TOKEN = "EXPIRED_ACCESS_TOKEN";
    public static final String ERROR_MESSAGE_INVALID_ACCESS_TOKEN = "Invalid Access Token";
    public static final String ERROR_MESSAGE_EXPIRED_ACCESS_TOKEN = "EXPIRED_ACCESS_TOKEN";
    public static final String AUTHCODE_EXPIRY_TIME = "authcode.expiry.time";
    public static final String ACCESSTOKEN_EXPIRY_TIME = "accesstoken.expiry.time";
    public static final String REFRESHTOKEN_EXPIRY_TIME = "refreshtoken.expiry.time";
    public static final String GRANT_TYPE = "grant.type";
    public static final String CONFIG_IS_EMPTY = "CONFIG_IS_EMPTY";


    public enum PAYMENT_STATUS {
        CASH(1, "Cash"),
        DEBIT_CARD(2, "Debit Card"),
        CREDIT_CARD(3, "Credit Card"),
        QRIS(4, "QRIS"),
        TRANSFER(5, "Transfer"),
        VOUCHER(6, "Voucher"),
        EMONEY(7, "E-Money"),
        UA(8, "UA"),
        OTHER(9, "Other");

        private final Integer value;
        private final String desc;

        private static final Map<Integer, PAYMENT_STATUS> BY_VALUE;

        static {
            Map<Integer, PAYMENT_STATUS> m = new HashMap<>();
            for (PAYMENT_STATUS p : values()) {
                m.put(p.value, p);
            }
            BY_VALUE = java.util.Collections.unmodifiableMap(m);
        }

        PAYMENT_STATUS(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public Integer getValue() { return value; }
        public String getDesc() { return desc; }

        // ini tetap boleh
        public static PAYMENT_STATUS fromValue(Integer v) {
            return BY_VALUE.get(v);
        }

        // ini kunci biar bisa terima "1" dari request JSON
        public static PAYMENT_STATUS fromCode(String code) {
            if (code == null || code.isBlank()) return null;
            try {
                int v = Integer.parseInt(code.trim());
                return fromValue(v);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }



    public enum CHANNEL {
        ONLINE(1, "Online"),
        OFFLINE(2, "Offline");


        private final Integer value;
        private final String desc;
        private static final Map map = new HashMap<>();

        CHANNEL(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        static {
            for (CHANNEL pageType : CHANNEL.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static CHANNEL valueOf(int pageType) {
            return (CHANNEL) map.get(pageType);
        }

        public Integer getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

}
