package zerodowntime;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    /**
     * Create and initialize a database if it doesn't exist
     */
    public static Jdbi createDatabase(String dbPath) {
        try {
            Path path = Paths.get(dbPath.replace("jdbc:sqlite:", ""));
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            // Check if DB exists
            boolean needsInit = !Files.exists(path);

            // Create Jdbi instance
            Jdbi jdbi = Jdbi.create(dbPath);
            jdbi.installPlugin(new SqlObjectPlugin());

            // Initialize schema if new database
            if (needsInit) {
                System.out.println("Initializing new database: " + dbPath);
                initializeSchema(dbPath);
            }

            return jdbi;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create database: " + dbPath, e);
        }
    }

    /**
     * Initialize database schema
     */
    private static void initializeSchema(String dbUrl) throws Exception {
        InputStream schemaStream = DatabaseManager.class.getResourceAsStream("/schema.sql");
        if (schemaStream == null) {
            throw new RuntimeException("schema.sql not found in resources");
        }

        String schema = new String(schemaStream.readAllBytes(), StandardCharsets.UTF_8);

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            for (String statement : schema.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        }
    }
}