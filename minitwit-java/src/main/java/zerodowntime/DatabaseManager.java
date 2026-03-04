package zerodowntime;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {

    public static Jdbi createDatabase() {
        // Pull variables injected by Docker Compose
        String url = System.getenv("JDBC_URL");
        String user = System.getenv("JDBC_USER");
        String pass = System.getenv("JDBC_PASS");

        if (url == null || user == null || pass == null) {
            throw new RuntimeException("Database environment variables (JDBC_URL, JDBC_USER, JDBC_PASS) are missing!");
        }

        Jdbi jdbi = Jdbi.create(url, user, pass);

        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new PostgresPlugin());

        // Initialize tables if they don't exist
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