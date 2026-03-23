import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.example.ShoppingCart;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shopping Cart Tests")
class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    // ==== Item Cost Calculation Tests ==

    @Test
    @DisplayName("Calculate single item cost: 10.00 × 5 = 50.00")
    void testCalculateSingleItemCost() {
        double result = cart.calculateItemCost(10.0, 5);
        assertEquals(50.0, result, "Price × Quantity should equal 50.0");
    }

    @Test
    @DisplayName("Calculate item cost with decimal values: 15.50 × 3 = 46.50")
    void testCalculateItemCostWithDecimals() {
        double result = cart.calculateItemCost(15.50, 3);
        assertEquals(46.50, result, 0.01, "Decimal calculation should be precise");
    }

    @Test
    @DisplayName("Calculate item cost with zero quantity: 10.00 × 0 = 0.00")
    void testCalculateItemCostWithZeroQuantity() {
        double result = cart.calculateItemCost(10.0, 0);
        assertEquals(0.0, result, "Zero quantity should result in 0.0 cost");
    }

    @Test
    @DisplayName("Calculate item cost with zero price: 0.00 × 5 = 0.00")
    void testCalculateItemCostWithZeroPrice() {
        double result = cart.calculateItemCost(0.0, 5);
        assertEquals(0.0, result, "Zero price should result in 0.0 cost");
    }

    @Test
    @DisplayName("Calculate item cost with both zero: 0.00 × 0 = 0.00")
    void testCalculateItemCostWithBothZero() {
        double result = cart.calculateItemCost(0.0, 0);
        assertEquals(0.0, result, "Zero price and quantity should result in 0.0");
    }

    @Test
    @DisplayName("Calculate item cost with large values: 999.99 × 100 = 99999.00")
    void testCalculateItemCostWithLargeValues() {
        double result = cart.calculateItemCost(999.99, 100);
        assertEquals(99999.0, result, 0.01, "Large values should calculate correctly");
    }

    // == Negative Value Tests ====

    @Test
    @DisplayName("Negative price should throw IllegalArgumentException")
    void testNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.calculateItemCost(-10.0, 5),
                "Negative price should throw exception");
    }

    @Test
    @DisplayName("Negative quantity should throw IllegalArgumentException")
    void testNegativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.calculateItemCost(10.0, -5),
                "Negative quantity should throw exception");
    }

    @Test
    @DisplayName("Both negative should throw IllegalArgumentException")
    void testBothNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.calculateItemCost(-10.0, -5),
                "Negative values should throw exception");
    }

    // == Add Item Tests ==

    @Test
    @DisplayName("Add single item to cart")
    void testAddSingleItem() {
        cart.addItem(10.0, 5);
        assertEquals(1, cart.getItemCount(), "Cart should have 1 item");
    }

    @Test
    @DisplayName("Add multiple items to cart")
    void testAddMultipleItems() {
        cart.addItem(10.0, 5);
        cart.addItem(20.0, 3);
        cart.addItem(15.50, 2);
        assertEquals(3, cart.getItemCount(), "Cart should have 3 items");
    }

    @Test
    @DisplayName("Add item with negative price should throw exception")
    void testAddItemWithNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(-10.0, 5),
                "Adding item with negative price should throw exception");
    }

    @Test
    @DisplayName("Add item with negative quantity should throw exception")
    void testAddItemWithNegativeQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(10.0, -5),
                "Adding item with negative quantity should throw exception");
    }

    // == Total Cost Tests =

    @Test
    @DisplayName("Calculate total cost of single item: 10.00 × 5 = 50.00")
    void testCalculateTotalCostSingleItem() {
        cart.addItem(10.0, 5);
        double result = cart.calculateTotalCost();
        assertEquals(50.0, result, "Total cost should be 50.0");
    }

    @Test
    @DisplayName("Calculate total cost of multiple items")
    void testCalculateTotalCostMultipleItems() {
        cart.addItem(10.0, 5);    // 50.0
        cart.addItem(20.0, 3);    // 60.0
        cart.addItem(15.50, 2);   // 31.0
        // Total: 141.0
        double result = cart.calculateTotalCost();
        assertEquals(141.0, result, 0.01, "Total cost should be 141.0");
    }

    @Test
    @DisplayName("Calculate total cost of empty cart: 0.00")
    void testCalculateTotalCostEmptyCart() {
        double result = cart.calculateTotalCost();
        assertEquals(0.0, result, "Empty cart should have 0.0 total cost");
    }

    @Test
    @DisplayName("Calculate total cost with decimal precision")
    void testCalculateTotalCostWithDecimals() {
        cart.addItem(9.99, 2);    // 19.98
        cart.addItem(5.50, 3);    // 16.50
        // Total: 36.48
        double result = cart.calculateTotalCost();
        assertEquals(36.48, result, 0.01, "Decimal total should be precise");
    }

    @Test
    @DisplayName("Calculate total cost with zero values")
    void testCalculateTotalCostWithZeros() {
        cart.addItem(0.0, 5);
        cart.addItem(10.0, 0);
        cart.addItem(0.0, 0);
        double result = cart.calculateTotalCost();
        assertEquals(0.0, result, "Cart with zero values should total 0.0");
    }

    // =Clear Cart Tests =

    @Test
    @DisplayName("Clear cart removes all items")
    void testClearCart() {
        cart.addItem(10.0, 5);
        cart.addItem(20.0, 3);
        cart.clear();
        assertEquals(0, cart.getItemCount(), "Cart should be empty after clear");
    }

    @Test
    @DisplayName("Clear cart resets total cost to zero")
    void testClearCartResetsTotalCost() {
        cart.addItem(10.0, 5);
        cart.addItem(20.0, 3);
        cart.clear();
        double result = cart.calculateTotalCost();
        assertEquals(0.0, result, "Total cost should be 0.0 after clear");
    }

    @Test
    @DisplayName("Clear empty cart should not throw exception")
    void testClearEmptyCartNoException() {
        assertDoesNotThrow(() -> cart.clear(), "Clearing empty cart should not throw exception");
    }

    // = Locale Selection Tests ==

    @Test
    @DisplayName("Select English locale")
    void testSelectEnglishLocale() {
        Locale result = ShoppingCart.selectLocale("1");
        assertEquals(Locale.of("en", "US"), result, "Should select English US locale");
    }

    @Test
    @DisplayName("Select Finnish locale")
    void testSelectFinnishLocale() {
        Locale result = ShoppingCart.selectLocale("2");
        assertEquals(Locale.of("fi", "FI"), result, "Should select Finnish locale");
    }

    @Test
    @DisplayName("Select Swedish locale")
    void testSelectSwedishLocale() {
        Locale result = ShoppingCart.selectLocale("3");
        assertEquals(Locale.of("sv", "SE"), result, "Should select Swedish locale");
    }

    @Test
    @DisplayName("Select Japanese locale")
    void testSelectJapaneseLocale() {
        Locale result = ShoppingCart.selectLocale("4");
        assertEquals(Locale.of("ja", "JP"), result, "Should select Japanese locale");
    }

    @Test
    @DisplayName("Invalid locale selection defaults to English")
    void testInvalidLocaleDefaultsToEnglish() {
        Locale result = ShoppingCart.selectLocale("99");
        assertEquals(Locale.ENGLISH, result, "Invalid selection should default to English");
    }

    @Test
    @DisplayName("Whitespace in locale selection should be trimmed")
    void testLocaleSelectionWithWhitespace() {
        Locale result = ShoppingCart.selectLocale("  1  ");
        assertEquals(Locale.of("en", "US"), result, "Whitespace should be trimmed");
    }

    // == Integration Tests ==

    @Test
    @DisplayName("Full shopping workflow: add items and calculate total")
    void testFullShoppingWorkflow() {
        // Add 3 items
        cart.addItem(25.00, 2);   // 50.00
        cart.addItem(15.50, 4);   // 62.00
        cart.addItem(30.00, 1);   // 30.00

        // Verify item count
        assertEquals(3, cart.getItemCount(), "Should have 3 items");

        // Verify total
        double total = cart.calculateTotalCost();
        assertEquals(142.00, total, 0.01, "Total should be 142.00");

        // Clear and verify
        cart.clear();
        assertEquals(0, cart.getItemCount(), "Cart should be empty after clear");
        assertEquals(0.0, cart.calculateTotalCost(), "Total should be 0.0 after clear");
    }

    @Test
    @DisplayName("Realistic shopping scenario with typical items")
    void testRealisticShoppingScenario() {
        // Simulate grocery shopping
        cart.addItem(2.99, 3);    // Apples: 8.97
        cart.addItem(4.50, 2);    // Milk: 9.00
        cart.addItem(1.99, 4);    // Bread: 7.96
        cart.addItem(12.99, 1);   // Cheese: 12.99

        double total = cart.calculateTotalCost();
        assertEquals(38.92, total, 0.01, "Realistic shopping total should be 38.92");
    }
}
