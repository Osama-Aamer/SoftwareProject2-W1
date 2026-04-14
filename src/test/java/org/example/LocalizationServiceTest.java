package org.example;

import org.example.DatabaseConnection;
import org.example.service.LocalizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


@DisplayName("LocalizationService Tests")
class LocalizationServiceTest {

    /** Skip the test if the DB cannot be reached. */
    private static void assumeDbAvailable() {
        try (Connection c = DatabaseConnection.getConnection()) {
            assumeTrue(c != null && !c.isClosed(), "Skipping: DB not available.");
        } catch (SQLException e) {
            assumeTrue(false, "Skipping: DB unavailable — " + e.getMessage());
        }
    }

    @Test
    @DisplayName("getLocalizedStrings: returns non-empty map for en_US (DB)")
    void testGetLocalizedStrings_english_db() {
        assumeDbAvailable();
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("en", "US"));
        assertNotNull(strings, "Result map should not be null");
        assertFalse(strings.isEmpty(), "Result map should not be empty for en_US");
    }

    @Test
    @DisplayName("getLocalizedStrings: returns non-empty map for fi_FI (DB)")
    void testGetLocalizedStrings_finnish_db() {
        assumeDbAvailable();
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("fi", "FI"));
        assertNotNull(strings);
        assertFalse(strings.isEmpty(), "Result map should not be empty for fi_FI");
    }

    @Test
    @DisplayName("getLocalizedStrings: returns non-empty map for ar_SA (DB)")
    void testGetLocalizedStrings_arabic_db() {
        assumeDbAvailable();
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("ar", "SA"));
        assertNotNull(strings);
        assertFalse(strings.isEmpty(), "Result map should not be empty for ar_SA");
    }

    @Test
    @DisplayName("getLocalizedStrings: result map contains essential keys (DB)")
    void testGetLocalizedStrings_containsEssentialKeys() {
        assumeDbAvailable();
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("en", "US"));

        String[] requiredKeys = {
                "app.title", "language.select", "items.prompt",
                "item.label", "item.price", "item.quantity",
                "total.cost", "button.calculate", "button.clear", "button.exit"
        };
        for (String key : requiredKeys) {
            assertTrue(strings.containsKey(key),
                    "Map should contain key: " + key);
        }
    }

    @Test
    @DisplayName("getLocalizedStrings: all values in map are non-null and non-empty")
    void testGetLocalizedStrings_allValuesNonEmpty() {
        assumeDbAvailable();
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("en", "US"));
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            assertNotNull(entry.getValue(), "Value for key '" + entry.getKey() + "' should not be null");
            assertFalse(entry.getValue().isBlank(), "Value for key '" + entry.getKey() + "' should not be blank");
        }
    }

    // -----------------------------------------------------------------------
    // Fallback tests — no DB required (always run)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getLocalizedStrings: always returns a non-null map (fallback path)")
    void testGetLocalizedStrings_neverReturnsNull() {
        // This exercises the ResourceBundle / hard-coded fallback path
        // Use an obscure locale so the DB definitely has no rows for it
        Locale obscure = Locale.of("xx", "XX");
        Map<String, String> strings = LocalizationService.getLocalizedStrings(obscure);
        assertNotNull(strings, "Should never return null — must at least return English defaults");
    }

    @Test
    @DisplayName("getLocalizedStrings: fallback map contains app.title")
    void testGetLocalizedStrings_fallbackContainsTitle() {
        Locale obscure = Locale.of("xx", "XX");
        Map<String, String> strings = LocalizationService.getLocalizedStrings(obscure);
        assertTrue(strings.containsKey("app.title"),
                "Even the fallback map should contain 'app.title'");
    }

    @Test
    @DisplayName("getLocalizedStrings: Swedish locale returns map (ResourceBundle fallback)")
    void testGetLocalizedStrings_swedish() {
        // sv_SE is in the .properties files so this tests the ResourceBundle path
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("sv", "SE"));
        assertNotNull(strings);
        assertFalse(strings.isEmpty(), "Swedish strings should load from ResourceBundle");
    }

    @Test
    @DisplayName("getLocalizedStrings: Japanese locale returns map")
    void testGetLocalizedStrings_japanese() {
        Map<String, String> strings = LocalizationService.getLocalizedStrings(Locale.of("ja", "JP"));
        assertNotNull(strings);
        assertFalse(strings.isEmpty(), "Japanese strings should not be empty");
    }
}
