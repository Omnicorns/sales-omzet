package com.sarinah.tenantsalesomzet.model.projection;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public interface PostGetTokenView {
    String getAccessToken();

    /**
     * Hibernate 7 (Spring Boot 4) mengembalikan kolom TIMESTAMP dari native query sebagai
     * {@link LocalDateTime}; Spring Data tidak bisa memproyeksikannya langsung ke {@link Timestamp}.
     */
    LocalDateTime getAccessTokenExpiryTime();

    String getScopes();
    String getAppId();

    /** Nilai kedaluwarsa dalam tipe lama ({@link Timestamp}) untuk kode yang sudah ada. */
    default Timestamp accessTokenExpiryTimestamp() {
        LocalDateTime value = getAccessTokenExpiryTime();
        return value == null ? null : Timestamp.valueOf(value);
    }
}
