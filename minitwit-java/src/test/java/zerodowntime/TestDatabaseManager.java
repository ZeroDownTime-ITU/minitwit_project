package zerodowntime;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestDatabaseManager {

    public static Jdbi createTestDatabase() {
        // Unique name per call
        String uniqueDb = "testdb_" + java.util.UUID.randomUUID().toString().replace("-", "");

        String url = "jdbc:h2:mem:" + uniqueDb
                + ";MODE=PostgreSQL"
                + ";DATABASE_TO_LOWER=TRUE"
                + ";DEFAULT_NULL_ORDERING=HIGH"
                + ";DB_CLOSE_DELAY=-1";

        Jdbi jdbi = Jdbi.create(url);
        jdbi.installPlugin(new SqlObjectPlugin());

        initializeSchema(jdbi);

        return jdbi;
    }

    private static void initializeSchema(Jdbi jdbi) {
        jdbi.useHandle(handle -> {
            try (InputStream is = TestDatabaseManager.class.getResourceAsStream("/schema.sql")) {
                if (is == null)
                    throw new RuntimeException("schema.sql not found!");
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                for (String sql : content.split(";")) {
                    if (!sql.trim().isEmpty()) {
                        handle.execute(sql);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize test schema", e);
            }
        });
    }
}