package com.revplay;

import com.revplay.util.DBConnection;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;

public class AppTest {

    @Test
    @DisplayName("Verify Database Connectivity")
    void testDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Database connection should not be null");
            assertFalse(conn.isClosed(), "Database connection should be open");
        } catch (Exception e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
}
