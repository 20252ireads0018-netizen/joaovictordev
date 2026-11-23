package br.com.ifba.infrastructure.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/**
 * Versionamento do banco com Flyway. As migrations ficam em
 * src/main/resources/db/migration (V1__..., V2__..., V3__...) e sao aplicadas
 * em sequencia no inicio da aplicacao, antes de o Hibernate validar o schema.
 *
 * A conexao vem do application.properties, a mesma usada pelo JPA.
 */
public final class MigracaoBanco {

    private static final String LOCAL_MIGRATIONS = "classpath:db/migration";

    private MigracaoBanco() {
    }

    /** Aplica as migrations pendentes no banco configurado. */
    public static MigrateResult migrar() {
        Flyway flyway = Flyway.configure()
                .dataSource(ConfiguracaoBanco.url(), ConfiguracaoBanco.usuario(), ConfiguracaoBanco.senha())
                .locations(LOCAL_MIGRATIONS)
                .load();
        return flyway.migrate();
    }
}
