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

    public void clear() {
        prices.clear();
        quantities.clear();
    }


    public int getItemCount() {
        return prices.size();
    }


    public static Locale selectLocale(String choice) {
        return switch (choice.trim()) {
            case "1" -> Locale.of("en", "US");
            case "2" -> Locale.of("fi", "FI");
            case "3" -> Locale.of("sv", "SE");
            case "4" -> Locale.of("ja", "JP");
            default -> Locale.ENGLISH;
        };
    }


    // Main method for console interaction
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        ShoppingCart cart = new ShoppingCart();


        System.out.println("Select language (1=English, 2=Finnish, 3=Swedish, 4=Japanese):");
        String languageChoice = scanner.nextLine().trim();


        Locale locale = selectLocale(languageChoice);


        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", locale);


        System.out.println(messages.getString("items.prompt"));
        int numItems = 0;
        while (true) {
            try {
                numItems = Integer.parseInt(scanner.nextLine().trim());
                if (numItems <= 0) {
                    System.out.println("Please enter a positive number.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }


        for (int i = 0; i < numItems; i++) {

            System.out.println(messages.getString("item.price").replace("{0}", String.valueOf(i + 1)));
            double price = 0;
            while (true) {
                try {
                    price = Double.parseDouble(scanner.nextLine().trim());
                    if (price < 0) {
                        System.out.println("Price cannot be negative.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid price.");
                }
            }

            System.out.println(messages.getString("item.quantity").replace("{0}", String.valueOf(i + 1)));
            int quantity = 0;
            while (true) {
                try {
                    quantity = Integer.parseInt(scanner.nextLine().trim());
                    if (quantity < 0) {
                        System.out.println("Quantity cannot be negative.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid quantity.");
                }
            }

            cart.addItem(price, quantity);
            double itemTotal = cart.calculateItemCost(price, quantity);
            System.out.println(messages.getString("item.total") + " " + String.format("%.2f", itemTotal));
        }


        double totalCost = cart.calculateTotalCost();
        System.out.println("\n" + messages.getString("total.cost") + " " + String.format("%.2f", totalCost) + " " + messages.getString("currency"));

        scanner.close();
    }
}
