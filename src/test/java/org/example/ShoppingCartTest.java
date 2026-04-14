package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ShoppingCart}.
 * Covers: addItem, calculateItemCost, calculateTotalCost,
 *         getItemCosts, clear, getItemCount – including edge cases.
 */
@DisplayName("ShoppingCart Tests")
class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    // -----------------------------------------------------------------------
    // calculateItemCost
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("calculateItemCost: normal price and quantity")
    void testCalculateItemCostNormal() {
        assertEquals(50.0, cart.calculateItemCost(10.0, 5), 0.001);
    }

    @Test
    @DisplayName("calculateItemCost: zero price returns 0")
    void testCalculateItemCostZeroPrice() {
        assertEquals(0.0, cart.calculateItemCost(0.0, 5), 0.001);
    }

    @Test
    @DisplayName("calculateItemCost: zero quantity returns 0")
    void testCalculateItemCostZeroQuantity() {
        assertEquals(0.0, cart.calculateItemCost(9.99, 0), 0.001);
    }

    @Test
    @DisplayName("calculateItemCost: negative price throws IllegalArgumentException")
    void testCalculateItemCostNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.calculateItemCost(-1.0, 3));
    }

    @Test
    @DisplayName("calculateItemCost: negative quantity throws IllegalArgumentException")
    void testCalculateItemCostNegativeQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.calculateItemCost(5.0, -2));
    }

    @Test
    @DisplayName("calculateItemCost: fractional price")
    void testCalculateItemCostFractional() {
        assertEquals(7.47, cart.calculateItemCost(2.49, 3), 0.001);
    }

    // -----------------------------------------------------------------------
    // addItem + getItemCount
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addItem: count increases correctly")
    void testAddItemIncreasesCount() {
        assertEquals(0, cart.getItemCount());
        cart.addItem(5.0, 2);
        assertEquals(1, cart.getItemCount());
        cart.addItem(3.0, 4);
        assertEquals(2, cart.getItemCount());
    }

    @Test
    @DisplayName("addItem: negative price throws IllegalArgumentException")
    void testAddItemNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(-0.01, 1));
    }

    @Test
    @DisplayName("addItem: negative quantity throws IllegalArgumentException")
    void testAddItemNegativeQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(10.0, -1));
    }

    @Test
    @DisplayName("addItem: zero price and zero quantity accepted")
    void testAddItemZeroValues() {
        assertDoesNotThrow(() -> cart.addItem(0.0, 0));
        assertEquals(1, cart.getItemCount());
    }

    // -----------------------------------------------------------------------
    // calculateTotalCost
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("calculateTotalCost: empty cart returns 0")
    void testCalculateTotalCostEmpty() {
        assertEquals(0.0, cart.calculateTotalCost(), 0.001);
    }

    @Test
    @DisplayName("calculateTotalCost: single item")
    void testCalculateTotalCostSingleItem() {
        cart.addItem(12.5, 4);
        assertEquals(50.0, cart.calculateTotalCost(), 0.001);
    }

    @Test
    @DisplayName("calculateTotalCost: multiple items")
    void testCalculateTotalCostMultipleItems() {
        cart.addItem(10.0, 2);  // 20
        cart.addItem(5.0,  3);  // 15
        cart.addItem(0.99, 1);  //  0.99
        assertEquals(35.99, cart.calculateTotalCost(), 0.001);
    }

    @Test
    @DisplayName("calculateTotalCost: large cart")
    void testCalculateTotalCostLargeCart() {
        for (int i = 1; i <= 20; i++) {
            cart.addItem(i, 1);   // sum 1..20 = 210
        }
        assertEquals(210.0, cart.calculateTotalCost(), 0.001);
    }

    // -----------------------------------------------------------------------
    // getItemCosts
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getItemCosts: empty cart returns empty list")
    void testGetItemCostsEmpty() {
        assertTrue(cart.getItemCosts().isEmpty());
    }

    @Test
    @DisplayName("getItemCosts: returns correct subtotals")
    void testGetItemCostsValues() {
        cart.addItem(10.0, 3);  // 30
        cart.addItem(2.5,  4);  // 10
        List<Double> costs = cart.getItemCosts();
        assertEquals(2, costs.size());
        assertEquals(30.0, costs.get(0), 0.001);
        assertEquals(10.0, costs.get(1), 0.001);
    }

    // -----------------------------------------------------------------------
    // clear
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("clear: resets item count to 0")
    void testClearResetsCount() {
        cart.addItem(1.0, 1);
        cart.addItem(2.0, 2);
        cart.clear();
        assertEquals(0, cart.getItemCount());
    }

    @Test
    @DisplayName("clear: total cost is 0 after clear")
    void testClearResetsTotalCost() {
        cart.addItem(99.0, 10);
        cart.clear();
        assertEquals(0.0, cart.calculateTotalCost(), 0.001);
    }

    @Test
    @DisplayName("clear: cart usable again after clear")
    void testClearAllowsReuse() {
        cart.addItem(5.0, 2);
        cart.clear();
        cart.addItem(8.0, 3);
        assertEquals(1, cart.getItemCount());
        assertEquals(24.0, cart.calculateTotalCost(), 0.001);
    }
}
