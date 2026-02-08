package com.revplay.integration;

import com.revplay.util.DBConnection;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    @DisplayName("Verify Atomicity: No partial register")
    void testAtomicRegistration() throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction

        try {
            // 1. Insert User
            String userSql = "INSERT INTO users (email, password_hash, username) VALUES ('tx@test.com', 'hash', 'txuser')";
            conn.createStatement().executeUpdate(userSql);

            // 2. Simulate failure before commit (e.g. invalid Artist insert)
            String failSql = "INSERT INTO artists (user_id, bio) VALUES (-999, 'Invalid ID')";
            conn.createStatement().executeUpdate(failSql);

            conn.commit();
        } catch (SQLException e) {
            conn.rollback(); // Undo everything
        } finally {
            conn.setAutoCommit(true);
        }

        // Final Check: Ensure user 'tx@test.com' does NOT exist
        String checkSql = "SELECT count(*) FROM users WHERE email = 'tx@test.com'";
        ResultSet rs = conn.createStatement().executeQuery(checkSql);
        rs.next();
        assertEquals(0, rs.getInt(1), "Rollback failed: Partial user data found in DB!");
    }
}
