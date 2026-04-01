package org.example;

import java.util.*;

public class ShoppingCart {
    private List<Double> prices = new ArrayList<>();
    private List<Integer> quantities = new ArrayList<>();

    public double calculateItemCost(double price, int quantity) {
        if (price < 0 || quantity < 0) {
            throw new IllegalArgumentException("Price and quantity cannot be negative");
        }
        return price * quantity;
    }

    public void addItem(double price, int quantity) {
        if (price < 0 || quantity < 0) {
            throw new IllegalArgumentException("Price and quantity cannot be negative");
        }
        prices.add(price);
        quantities.add(quantity);
    }

    public double calculateTotalCost() {
        double total = 0;
        for (int i = 0; i < prices.size(); i++) {
            total += calculateItemCost(prices.get(i), quantities.get(i));
        }
        return total;
    }

    public List<Double> getItemCosts() {
        List<Double> costs = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            costs.add(calculateItemCost(prices.get(i), quantities.get(i)));
        }
        return costs;
    }

    public void clear() {
        prices.clear();
        quantities.clear();
    }

    public int getItemCount() {
        return prices.size();
    }
}
