package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Main#addMe(int, int)}.
 */
@DisplayName("Main – addMe Tests")
class MainTest {

    @Test
    @DisplayName("addMe: positive numbers")
    void testAddMePositive() {
        assertEquals(16, Main.addMe(12, 4));
    }

    @Test
    @DisplayName("addMe: adding zero")
    void testAddMeWithZero() {
        assertEquals(5, Main.addMe(5, 0));
        assertEquals(5, Main.addMe(0, 5));
    }

    @Test
    @DisplayName("addMe: negative numbers")
    void testAddMeNegative() {
        assertEquals(-3, Main.addMe(-5, 2));
        assertEquals(-7, Main.addMe(-3, -4));
    }

    @Test
    @DisplayName("addMe: large numbers")
    void testAddMeLarge() {
        assertEquals(2_000_000, Main.addMe(1_000_000, 1_000_000));
    }

    @Test
    @DisplayName("addMe: overflow check uses int arithmetic")
    void testAddMeOverflow() {
        // Java int overflow is defined behaviour
        int result = Main.addMe(Integer.MAX_VALUE, 1);
        assertEquals(Integer.MIN_VALUE, result);
    }
}
