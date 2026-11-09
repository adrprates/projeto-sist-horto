package com.bsh.backend_sist_horto.suporte;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Esvazia todas as tabelas do H2 antes de cada teste, garantindo que um teste
 * nunca dependa de dados deixados por outro.
 */
@Component
public class LimpezaBancoDados {

    private final JdbcTemplate jdbcTemplate;

    public LimpezaBancoDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void limpar() {
        List<String> tabelas = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE TABLE_TYPE = 'BASE TABLE' AND LOWER(TABLE_SCHEMA) = 'public'",
                String.class);

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        try {
            for (String tabela : tabelas) {
                jdbcTemplate.execute("TRUNCATE TABLE \"" + tabela + "\" RESTART IDENTITY");
            }
        } finally {
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }
}
