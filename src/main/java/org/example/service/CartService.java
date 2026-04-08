package org.example.service;

import org.example.DatabaseConnection;

import java.sql.*;
import java.util.List;


public class CartService {


    public static void saveCart(int totalItems, double totalCost, String language,
                                List<Double> prices, List<Integer> quantities) {
        String insertRecord = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        String insertItem   = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Insert the main cart record and retrieve generated ID
            long cartRecordId;
            try (PreparedStatement ps = conn.prepareStatement(insertRecord, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, totalItems);
                ps.setDouble(2, totalCost);
                ps.setString(3, language);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) {
                    System.err.println("CartService: failed to retrieve generated cart_record id.");
                    return;
                }
                cartRecordId = keys.getLong(1);
            }

            // Insert each cart item linked to the cart record
            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (int i = 0; i < prices.size(); i++) {
                    double price    = prices.get(i);
                    int    quantity = quantities.get(i);
                    double subtotal = price * quantity;

                    ps.setLong(1, cartRecordId);
                    ps.setInt(2, i + 1);           // item_number (1-based)
                    ps.setDouble(3, price);
                    ps.setInt(4, quantity);
                    ps.setDouble(5, subtotal);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            System.out.println("CartService: cart record #" + cartRecordId + " saved successfully.");

        } catch (SQLException e) {
            System.err.println("CartService: database error — " + e.getMessage());
            e.printStackTrace();
        }
    }
}
