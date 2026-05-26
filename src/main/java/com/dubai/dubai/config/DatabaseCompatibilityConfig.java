package com.dubai.dubai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCompatibilityConfig {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCompatibilityConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ajustarColumnasLegacy() {
        if (existeColumnaPagoIdNoNula()) {
            jdbcTemplate.execute("ALTER TABLE reservas ALTER COLUMN pago_id DROP NOT NULL");
        }
    }

    private boolean existeColumnaPagoIdNoNula() {
        Integer total = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = current_schema()
                  AND table_name = 'reservas'
                  AND column_name = 'pago_id'
                  AND is_nullable = 'NO'
                """, Integer.class);
        return total != null && total > 0;
    }
}
