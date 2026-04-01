package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shopping Cart Tests")
public class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    @DisplayName("Calculate single item cost correctly")
    void testCalculateItemCost() {
        double result = cart.calculateItemCost(10.0, 5);
        assertEquals(50.0, result, "10 * 5 should equal 50");
    }

    @Test
    @DisplayName("Calculate item cost with decimals")
    void testCalculateItemCostDecimal() {
        double result = cart.calculateItemCost(9.99, 3);
        assertEquals(29.97, result, 0.01, "9.99 * 3 should equal 29.97");
    }

    @Test
    @DisplayName("Calculate item cost with zero quantity")
    void testCalculateItemCostZeroQuantity() {
        double result = cart.calculateItemCost(10.0, 0);
        assertEquals(0.0, result, "Any price * 0 should equal 0");
    }

    @Test
    @DisplayName("Throw exception for negative price")
    void testNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.calculateItemCost(-5.0, 2);
        });
    }

    @Test
    @DisplayName("Throw exception for negative quantity")
    void testNegativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.calculateItemCost(10.0, -2);
        });
    }

    @Test
    @DisplayName("Add item to cart")
    void testAddItem() {
        cart.addItem(15.0, 2);
        assertEquals(1, cart.getItemCount(), "Cart should have 1 item");
    }

    @Test
    @DisplayName("Add multiple items to cart")
    void testAddMultipleItems() {
        cart.addItem(10.0, 2);
        cart.addItem(20.0, 3);
        cart.addItem(5.0, 1);
        assertEquals(3, cart.getItemCount(), "Cart should have 3 items");
    }

    @Test
    @DisplayName("Calculate total cost correctly")
    void testCalculateTotalCost() {
        cart.addItem(10.0, 2);  // 20
        cart.addItem(15.0, 3);  // 45
        cart.addItem(5.0, 4);   // 20

        double total = cart.calculateTotalCost();
        assertEquals(85.0, total, "Total should be 85");
    }

    @Test
    @DisplayName("Calculate total cost with decimal prices")
    void testCalculateTotalCostDecimal() {
        cart.addItem(9.99, 2);   // 19.98
        cart.addItem(14.50, 2);  // 29.00

        double total = cart.calculateTotalCost();
        assertEquals(48.98, total, 0.01, "Total should be 48.98");
    }

    @Test
    @DisplayName("Empty cart has zero total")
    void testEmptyCartTotal() {
        double total = cart.calculateTotalCost();
        assertEquals(0.0, total, "Empty cart should have 0 total");
    }

    @Test
    @DisplayName("Clear cart removes all items")
    void testClearCart() {
        cart.addItem(10.0, 2);
        cart.addItem(20.0, 3);
        cart.clear();

        assertEquals(0, cart.getItemCount(), "Cart should be empty after clear");
        assertEquals(0.0, cart.calculateTotalCost(), "Total should be 0 after clear");
    }

    @Test
    @DisplayName("Get item costs list")
    void testGetItemCosts() {
        cart.addItem(10.0, 2);  // 20
        cart.addItem(15.0, 3);  // 45

        var costs = cart.getItemCosts();
        assertEquals(2, costs.size(), "Should have 2 item costs");
        assertEquals(20.0, costs.get(0), "First item cost should be 20");
        assertEquals(45.0, costs.get(1), "Second item cost should be 45");
    }

    @Test
    @DisplayName("Throw exception when adding item with negative price")
    void testAddNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(-10.0, 2);
        });
    }

    @Test
    @DisplayName("Throw exception when adding item with negative quantity")
    void testAddNegativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(10.0, -2);
        });
    }
}
