package zerodowntime;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestDatabaseManager {

    public static DSLContext createTestDatabase() {
        // Unique name per call
        String uniqueDb = "testdb_" + java.util.UUID.randomUUID().toString().replace("-", "");

        String url = "jdbc:h2:mem:" + uniqueDb
                + ";MODE=PostgreSQL"
                + ";DATABASE_TO_LOWER=TRUE"
                + ";DEFAULT_NULL_ORDERING=HIGH"
                + ";DB_CLOSE_DELAY=-1";

        try {
            Connection conn = DriverManager.getConnection(url, "sa", "");
            initializeSchema(conn);

            return DSL.using(conn, SQLDialect.H2);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create jOOQ test database", e);
        }
    }

    private static void initializeSchema(Connection conn) {
        try (InputStream is = TestDatabaseManager.class.getResourceAsStream("/schema.sql");
                Statement stmt = conn.createStatement()) {

            if (is == null)
                throw new RuntimeException("schema.sql not found!");

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            for (String sql : content.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test schema", e);
        }
    }
}