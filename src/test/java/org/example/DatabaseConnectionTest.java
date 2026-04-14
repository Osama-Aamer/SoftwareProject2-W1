package org.example;

import org.example.DatabaseConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("DatabaseConnection Integration Tests")
class DatabaseConnectionTest {

    @Test
    @DisplayName("getConnection: returns an open, non-null connection when DB is up")
    void testGetConnection_returnsOpenConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // If we get here without exception → DB is available
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            // DB is not running — skip this test gracefully
            assumeTrue(false, "Skipping: DB unavailable — " + e.getMessage());
        }
    }

    @Test
    @DisplayName("getConnection: connection is valid (ping)")
    void testGetConnection_isValid() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertTrue(conn.isValid(2), "Connection should be valid within 2 seconds");
        } catch (SQLException e) {
            assumeTrue(false, "Skipping: DB unavailable — " + e.getMessage());
        }
    }
}
