package zerodowntime;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.jdbi.v3.postgres.PostgresPlugin;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {

    public static Jdbi createDatabase() {
        String url = System.getenv("JDBC_URL");
        String user = System.getenv("JDBC_USER");
        String pass = System.getenv("JDBC_PASS");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(3000);
        config.setIdleTimeout(600000);

        HikariDataSource ds = new HikariDataSource(config);
        Jdbi jdbi = Jdbi.create(ds);

        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new PostgresPlugin());

        initializeSchema(jdbi);

        return jdbi;
    }

    private static void initializeSchema(Jdbi jdbi) {
        jdbi.useHandle(handle -> {
            try (InputStream is = DatabaseManager.class.getResourceAsStream("/schema.sql")) {
                if (is == null)
                    throw new RuntimeException("schema.sql not found!");

                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                for (String sql : content.split(";")) {
                    if (!sql.trim().isEmpty()) {
                        handle.execute(sql);
                    }
                }
                System.out.println("Database schema verified/initialized.");
            } catch (Exception e) {
                System.err.println("Schema info: " + e.getMessage());
            }
        });
    }
}