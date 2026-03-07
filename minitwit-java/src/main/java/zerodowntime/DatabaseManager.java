package zerodowntime;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;

public class DatabaseManager {
    private static DSLContext dslContext;
    private static HikariDataSource dataSource;

    public static void init(DataSource dataSource) {
        dslContext = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    public static void init() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("JDBC_URL"));
        config.setUsername(System.getenv("JDBC_USER"));
        config.setPassword(System.getenv("JDBC_PASS"));

        // --- 1 vCPU / 1GB RAM Optimizations ---
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(3000); // Wait 3s max for a connection, then error out
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes

        dataSource = new HikariDataSource(config);
        dslContext = DSL.using(dataSource, SQLDialect.POSTGRES);

        initializeSchema();
    }

    public static DSLContext getDsl() {
        if (dslContext == null)
            init();
        return dslContext;
    }

    private static void initializeSchema() {
        try (InputStream is = DatabaseManager.class.getResourceAsStream("/schema.sql")) {
            if (is == null)
                throw new RuntimeException("schema.sql not found!");

            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    getDsl().execute(statement);
                }
            }

            System.out.println("Database schema verified/initialized via jOOQ.");
        } catch (Exception e) {
            System.err.println("Schema Initialization Error: " + e.getMessage());
        }
    }
}