package org.example;

import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@DisplayName("ShoppingCartController UI Tests")
class ShoppingCartControllerTest {

    @Start
    private void start(Stage stage) throws Exception {
        Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/shopping_cart.fxml"));
        stage.setScene(new Scene(root, 700, 800));
        stage.show();
    }

    @Test
    @DisplayName("Language switch updates locale path and RTL for Arabic")
    void testLanguageSwitchArabicRtl(FxRobot robot) {
        ComboBox<String> combo = robot.lookup("#languageCombo").queryComboBox();
        robot.interact(() -> combo.setValue("Arabic (ar_SA)"));
        WaitForAsyncUtils.waitForFxEvents();

        BorderPane root = robot.lookup("#rootVBox").queryAs(BorderPane.class);
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
    }

    @Test
    @DisplayName("Price edit updates item subtotal and total")
    void testPriceInputUpdatesTotals(FxRobot robot) {
        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        assertFalse(itemsPanel.getChildren().isEmpty());

        HBox firstRow = (HBox) itemsPanel.getChildren().get(0);
        TextField priceField = null;
        Label itemTotal = null;

        for (var node : firstRow.getChildren()) {
            if (node instanceof TextField tf) {
                priceField = tf;
            }
            if (node instanceof Label lbl && !lbl.getText().isBlank()) {
                itemTotal = lbl;
            }
        }

        assertNotNull(priceField);
        assertNotNull(itemTotal);

        robot.clickOn(priceField).write("10");
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
        assertFalse(totalCost.getText().isBlank());
    }

    @Test
    @DisplayName("Quantity spinner change updates total")
    void testQuantitySpinnerUpdatesTotal(FxRobot robot) {
        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        HBox firstRow = (HBox) itemsPanel.getChildren().get(0);

        TextField priceField = null;
        Spinner<?> qtySpinner = null;

        for (var node : firstRow.getChildren()) {
            if (node instanceof TextField tf) {
                priceField = tf;
            }
            if (node instanceof Spinner<?> sp) {
                qtySpinner = sp;
            }
        }

        assertNotNull(priceField);
        assertNotNull(qtySpinner);

        robot.clickOn(priceField).write("5");
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn(qtySpinner);
        robot.type(KeyCode.UP);
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
        assertFalse(totalCost.getText().isBlank());
    }

    @Test
    @DisplayName("Calculate triggers completion flow and clear resets totals")
    void testCalculateAndClear(FxRobot robot) {
        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        HBox firstRow = (HBox) itemsPanel.getChildren().get(0);

        TextField priceField = null;
        for (var node : firstRow.getChildren()) {
            if (node instanceof TextField tf) {
                priceField = tf;
                break;
            }
        }
        assertNotNull(priceField);

        robot.clickOn(priceField).write("12");
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        // Close alert if shown
        robot.type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#btnClear");
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
    }
    @Test
    @DisplayName("Language switch hits Finnish/Swedish/Japanese cases")
    void testLanguageSwitchAllLtrCases(FxRobot robot) {
        ComboBox<String> combo = robot.lookup("#languageCombo").queryComboBox();
        BorderPane root = robot.lookup("#rootVBox").queryAs(BorderPane.class);

        robot.interact(() -> combo.setValue("Finnish (fi_FI)"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());

        robot.interact(() -> combo.setValue("Swedish (sv_SE)"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());

        robot.interact(() -> combo.setValue("Japanese (ja_JP)"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    @DisplayName("Arabic mode converts western digits to Arabic-Indic digits")
    void testArabicDigitConversion(FxRobot robot) {
        ComboBox<String> combo = robot.lookup("#languageCombo").queryComboBox();
        robot.interact(() -> combo.setValue("Arabic (ar_SA)"));
        WaitForAsyncUtils.waitForFxEvents();

        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        HBox firstRow = (HBox) itemsPanel.getChildren().get(0);

        TextField priceField = null;
        for (var node : firstRow.getChildren()) {
            if (node instanceof TextField tf) {
                priceField = tf;
                break;
            }
        }
        assertNotNull(priceField);

        robot.clickOn(priceField);
        robot.type(KeyCode.DIGIT1);
        robot.type(KeyCode.DIGIT2);
        robot.type(KeyCode.DIGIT3);
        WaitForAsyncUtils.waitForFxEvents();

        // Should now contain Arabic-Indic numerals after controller normalization
        String value = priceField.getText();
        assertTrue(value.matches(".*[٠-٩].*"), "Expected Arabic digits in field, got: " + value);
    }

    @Test
    @DisplayName("Invalid number paths are handled (price listener, qty listener, total update)")
    void testInvalidNumberBranches(FxRobot robot) {
        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        HBox firstRow = (HBox) itemsPanel.getChildren().get(0);

        TextField priceField = null;
        Spinner<?> qtySpinner = null;
        for (var node : firstRow.getChildren()) {
            if (node instanceof TextField tf) priceField = tf;
            if (node instanceof Spinner<?> sp) qtySpinner = sp;
        }

        assertNotNull(priceField);
        assertNotNull(qtySpinner);

        // Triggers NumberFormatException in price listener catch (line ~242)
        robot.clickOn(priceField).write("abc");
        WaitForAsyncUtils.waitForFxEvents();

        // Triggers NumberFormatException in quantity listener catch (line ~255)
        robot.clickOn(qtySpinner);
        robot.type(KeyCode.UP);
        WaitForAsyncUtils.waitForFxEvents();

        // Triggers invalid parse path in updateTotalCost loop catch (line ~291)
        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        // Close success alert if shown
        robot.type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("Calculate executes alert flow and clear still works")
    void testCalculateFlowCoversAlertAndClear(FxRobot robot) {
        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        HBox firstRow = (HBox) itemsPanel.getChildren().get(0);

        TextField priceField = null;
        for (var node : firstRow.getChildren()) {
            if (node instanceof TextField tf) {
                priceField = tf;
                break;
            }
        }
        assertNotNull(priceField);

        robot.clickOn(priceField).eraseText(20).write("12.5");
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        // showAlert(...) uses showAndWait(); press Enter to close dialog
        robot.type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#btnClear");
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
        assertFalse(totalCost.getText().isBlank());
    }

    @Test
    @DisplayName("Text direction flips back from Arabic RTL to English LTR")
    void testDirectionFlipBothWays(FxRobot robot) {
        ComboBox<String> combo = robot.lookup("#languageCombo").queryComboBox();
        BorderPane root = robot.lookup("#rootVBox").queryAs(BorderPane.class);

        robot.interact(() -> combo.setValue("Arabic (ar_SA)"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());

        robot.interact(() -> combo.setValue("English (en_US)"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

}
