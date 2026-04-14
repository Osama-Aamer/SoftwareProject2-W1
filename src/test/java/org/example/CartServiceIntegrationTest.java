package org.example;

import org.example.service.CartService;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("CartService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartServiceIntegrationTest {

    /** Checks if DB is reachable; skips the test if not. */
    private static void assumeDbAvailable() {
        try (Connection c = DatabaseConnection.getConnection()) {
            assumeTrue(c != null && !c.isClosed(), "Skipping: DB not available.");
        } catch (SQLException e) {
            assumeTrue(false, "Skipping: DB unavailable — " + e.getMessage());
        }
    }

    /** Counts rows currently in cart_records. */
    private long countCartRecords() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM cart_records")) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    /** Deletes the last inserted cart_record (and its items via CASCADE). */
    private void deleteLastRecord() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("DELETE FROM cart_records WHERE id = (SELECT id FROM (SELECT MAX(id) AS id FROM cart_records) t)");
        }
    }

    // -----------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("saveCart: inserts a new record into cart_records")
    void testSaveCart_insertsRecord() throws SQLException {
        assumeDbAvailable();

        long before = countCartRecords();

        CartService.saveCart(
                2, 35.0, "en_US",
                List.of(10.0, 25.0),
                List.of(1, 1)
        );

        long after = countCartRecords();
        assertEquals(before + 1, after, "One new cart_record should have been inserted");

        // Clean up
        deleteLastRecord();
    }

    @Test
    @Order(2)
    @DisplayName("saveCart: inserts correct cart_items for the record")
    void testSaveCart_insertsCorrectItems() throws SQLException {
        assumeDbAvailable();

        CartService.saveCart(
                3, 60.0, "fi_FI",
                List.of(10.0, 20.0, 30.0),
                List.of(1, 1, 1)
        );

        // Verify items were inserted for the latest cart record
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*) FROM cart_items WHERE cart_record_id = (SELECT MAX(id) FROM cart_records)")) {
            assertTrue(rs.next());
            assertEquals(3, rs.getInt(1), "Should have 3 cart_items inserted");
        }

        // Clean up
        deleteLastRecord();
    }

    @Test
    @Order(3)
    @DisplayName("saveCart: subtotals are calculated correctly in DB")
    void testSaveCart_correctSubtotals() throws SQLException {
        assumeDbAvailable();

        CartService.saveCart(
                1, 50.0, "en_US",
                List.of(10.0),
                List.of(5)
        );

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT subtotal FROM cart_items WHERE cart_record_id = (SELECT MAX(id) FROM cart_records)")) {
            assertTrue(rs.next(), "Should have at least one item");
            assertEquals(50.0, rs.getDouble("subtotal"), 0.001, "Subtotal should be 10.0 * 5 = 50.0");
        }

        // Clean up
        deleteLastRecord();
    }

    @Test
    @Order(4)
    @DisplayName("saveCart: handles empty item list gracefully")
    void testSaveCart_emptyItems() throws SQLException {
        assumeDbAvailable();

        long before = countCartRecords();

        CartService.saveCart(0, 0.0, "en_US", List.of(), List.of());

        long after = countCartRecords();
        assertEquals(before + 1, after, "Record should still be inserted even with no items");

        // Clean up
        deleteLastRecord();
    }

    @Test
    @Order(5)
    @DisplayName("saveCart: stores the correct language code")
    void testSaveCart_correctLanguage() throws SQLException {
        assumeDbAvailable();

        CartService.saveCart(1, 15.0, "ja_JP", List.of(15.0), List.of(1));

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT language FROM cart_records WHERE id = (SELECT MAX(id) FROM cart_records)")) {
            assertTrue(rs.next());
            assertEquals("ja_JP", rs.getString("language"), "Language code should match");
        }

        // Clean up
        deleteLastRecord();
    }
}
