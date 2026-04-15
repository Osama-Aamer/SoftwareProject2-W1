package org.example.service;

import org.example.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


public class LocalizationService {

    public static Map<String, String> getLocalizedStrings(Locale locale) {
        // Build the language code used in the DB (e.g. "en_US", "ar_SA")
        String langCode = locale.getLanguage() + "_" + locale.getCountry();

        Map<String, String> strings = loadFromDatabase(langCode);

        if (!strings.isEmpty()) {
            System.out.println("LocalizationService: loaded " + strings.size()
                    + " strings from DB for locale '" + langCode + "'.");
            return strings;
        }

        // Fallback – try the .properties ResourceBundle
        System.out.println("LocalizationService: DB empty for '" + langCode
                + "', falling back to ResourceBundle.");
        return loadFromResourceBundle(locale);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private static Map<String, String> loadFromDatabase(String langCode) {
        Map<String, String> strings = new HashMap<>();
        String sql = "SELECT `key`, `value` FROM localization_strings WHERE language = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, langCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                strings.put(rs.getString("key"), rs.getString("value"));
            }
        } catch (Exception e) {
            System.err.println("LocalizationService: DB unavailable — " + e.getMessage());
        }
        return strings;
    }

    private static Map<String, String> loadFromResourceBundle(Locale locale) {
        Map<String, String> strings = new HashMap<>();
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);
            for (String key : bundle.keySet()) {
                strings.put(key, bundle.getString(key));
            }
        } catch (Exception e) {
            System.err.println("LocalizationService: ResourceBundle also failed — " + e.getMessage());
            try {
                ResourceBundle fallback = ResourceBundle.getBundle("MessagesBundle", Locale.of("en", "US"));
                for (String key : fallback.keySet()) {
                    strings.put(key, fallback.getString(key));
                }
            } catch (Exception ex) {
                // Hard-coded English defaults as last resort
                strings.put("app.title",           "Shopping Cart - Osama Aamer");
                strings.put("language.select",      "Select Language:");
                strings.put("items.prompt",         "Enter number of items:");
                strings.put("item.label",           "Item");
                strings.put("item.price",           "Price:");
                strings.put("item.quantity",        "Quantity:");
                strings.put("item.total",           "Total:");
                strings.put("total.cost",           "Total Cost:");
                strings.put("button.calculate",     "Calculate");
                strings.put("button.clear",         "Clear");
                strings.put("button.exit",          "Exit");
                strings.put("calculation.complete", "Calculation completed successfully!");
            }
        }
        return strings;
    }
}
