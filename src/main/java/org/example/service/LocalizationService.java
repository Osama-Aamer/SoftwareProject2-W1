package org.example.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocalizationService {
    

    public static Map<String, String> getLocalizedStrings(Locale locale) {
        Map<String, String> strings = new HashMap<>();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);

            for (String key : bundle.keySet()) {
                strings.put(key, bundle.getString(key));
            }
        } catch (Exception e) {
            System.err.println("Failed to load resource bundle for locale: " + locale);
            try {
                ResourceBundle fallback = ResourceBundle.getBundle("MessagesBundle", new Locale("en", "US"));
                for (String key : fallback.keySet()) {
                    strings.put(key, fallback.getString(key));
                }
            } catch (Exception ex) {
                // hardcoded defaults as last resort
                strings.put("app.title", "Shopping Cart - Osama Aamer");
                strings.put("language.select", "Select Language:");
                strings.put("items.prompt", "Enter number of items:");
                strings.put("item.label", "Item");
                strings.put("item.price", "Price:");
                strings.put("item.quantity", "Quantity:");
                strings.put("item.total", "Total:");
                strings.put("total.cost", "Total Cost:");
                strings.put("button.calculate", "Calculate");
                strings.put("button.clear", "Clear");
                strings.put("button.exit", "Exit");
                strings.put("calculation.complete", "Calculation completed successfully!");
            }
        }

        return strings;
    }
}
