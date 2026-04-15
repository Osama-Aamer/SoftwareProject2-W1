package org.example;

import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@DisplayName("ShoppingCartApp Tests")
class ShoppingCartAppTest {

    @Start
    private void start(Stage stage) throws Exception {
        new ShoppingCartApp().start(stage);
    }

    @Test
    @DisplayName("App starts with scene and title")
    void testAppStarts(FxRobot robot) {
        Stage stage = (Stage) robot.window(0);
        assertNotNull(stage.getScene());
        assertTrue(stage.isShowing());
        assertEquals("Shopping Cart - Osama Aamer", stage.getTitle());
    }
}
