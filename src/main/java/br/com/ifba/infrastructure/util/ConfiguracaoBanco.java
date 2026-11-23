package br.com.ifba.infrastructure.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Le as configuracoes de banco do application.properties. Nos testes, o
 * application.properties de src/test/resources vem antes no classpath e
 * aponta para o H2 em memoria.
 */
public final class ConfiguracaoBanco {

    private static final String ARQUIVO = "application.properties";

    static final String UNIDADE = "pgcd.persistence.unit";
    static final String URL = "spring.datasource.url";
    static final String USUARIO = "spring.datasource.username";
    static final String SENHA = "spring.datasource.password";
    static final String DRIVER = "spring.datasource.driver-class-name";
    static final String DDL = "spring.jpa.hibernate.ddl-auto";
    static final String MOSTRAR_SQL = "spring.jpa.show-sql";

    private static Properties propriedades;

    private ConfiguracaoBanco() {
    }

    static synchronized Properties propriedades() {
        if (propriedades == null) {
            Properties lidas = new Properties();
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ARQUIVO)) {
                if (in == null) {
                    throw new IllegalStateException(ARQUIVO + " nao encontrado no classpath.");
                }
                lidas.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Nao foi possivel ler " + ARQUIVO, e);
            }
            propriedades = lidas;
        }
        return propriedades;
    }

    /** Unidade de persistencia: a propriedade de sistema tem prioridade sobre o arquivo. */
    public static String unidade() {
        return System.getProperty(UNIDADE, propriedades().getProperty(UNIDADE, "pgcdPU"));
    }

    public static String url() {
        String url = propriedades().getProperty(URL);
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(URL + " nao definido em " + ARQUIVO);
        }
        return url;
    }

    public static String usuario() {
        return propriedades().getProperty(USUARIO, "");
    }

    public static String senha() {
        return propriedades().getProperty(SENHA, "");
    }

    /** Propriedades JPA que substituem as do persistence.xml ao criar a fabrica. */
    public static Map<String, String> propriedadesJpa() {
        Map<String, String> jpa = new HashMap<>();
        jpa.put("jakarta.persistence.jdbc.url", url());
        jpa.put("jakarta.persistence.jdbc.user", usuario());
        jpa.put("jakarta.persistence.jdbc.password", senha());
        String driver = propriedades().getProperty(DRIVER);
        if (driver != null && !driver.isBlank()) {
            jpa.put("jakarta.persistence.jdbc.driver", driver);
        }
        jpa.put("hibernate.hbm2ddl.auto", propriedades().getProperty(DDL, "validate"));
        jpa.put("hibernate.show_sql", propriedades().getProperty(MOSTRAR_SQL, "false"));
        return jpa;
    }
}
