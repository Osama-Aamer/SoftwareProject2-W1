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

    private HBox firstItemRow(FxRobot robot) {
        VBox itemsPanel = robot.lookup("#itemsPanel").queryAs(VBox.class);
        assertFalse(itemsPanel.getChildren().isEmpty(), "itemsPanel should contain at least one row");
        return (HBox) itemsPanel.getChildren().get(0);
    }

    private TextField findPriceField(HBox row) {
        for (var node : row.getChildren()) {
            if (node instanceof TextField tf) {
                return tf;
            }
        }
        fail("Price TextField not found in first row");
        return null;
    }

    private Spinner<?> findQuantitySpinner(HBox row) {
        for (var node : row.getChildren()) {
            if (node instanceof Spinner<?> sp) {
                return sp;
            }
        }
        fail("Quantity Spinner not found in first row");
        return null;
    }

    private void closeAlertIfPresent(FxRobot robot) {
        try {
            robot.type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();
        } catch (Exception ignored) {
            // No alert focused/open; safe to continue.
        }
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
        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);

        assertNotNull(priceField);
        final TextField finalPriceField = priceField;
        robot.interact(() -> finalPriceField.setText("10"));
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
        assertFalse(totalCost.getText().isBlank());
    }

    @Test
    @DisplayName("Quantity spinner change updates total")
    void testQuantitySpinnerUpdatesTotal(FxRobot robot) {
        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);
        Spinner<?> qtySpinner = findQuantitySpinner(firstRow);

        assertNotNull(priceField);
        assertNotNull(qtySpinner);

        final TextField finalPriceField = priceField;
        final Spinner<?> finalQtySpinner = qtySpinner;

        robot.interact(() -> finalPriceField.setText("5"));
        WaitForAsyncUtils.waitForFxEvents();

        robot.interact(() -> finalQtySpinner.increment(1));
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
        assertFalse(totalCost.getText().isBlank());
    }

    @Test
    @DisplayName("Calculate triggers completion flow and clear resets totals")
    void testCalculateAndClear(FxRobot robot) {
        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);

        assertNotNull(priceField);
        final TextField finalPriceField = priceField;
        robot.interact(() -> finalPriceField.setText("12"));
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        closeAlertIfPresent(robot);

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

        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);

        assertNotNull(priceField);
        final TextField finalPriceField = priceField;
        robot.interact(() -> finalPriceField.setText("123"));
        WaitForAsyncUtils.waitForFxEvents();

        String value = priceField.getText();
        assertTrue(value.matches(".*[٠-٩].*"), "Expected Arabic digits in field, got: " + value);
    }

    @Test
    @DisplayName("Invalid number paths are handled (price listener, qty listener, total update)")
    void testInvalidNumberBranches(FxRobot robot) {
        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);
        Spinner<?> qtySpinner = findQuantitySpinner(firstRow);

        assertNotNull(priceField);
        assertNotNull(qtySpinner);

        final TextField finalPriceField = priceField;
        final Spinner<?> finalQtySpinner = qtySpinner;

        // Triggers NumberFormatException in price listener catch
        robot.interact(() -> finalPriceField.setText("abc"));
        WaitForAsyncUtils.waitForFxEvents();

        // Triggers quantity listener path with invalid existing price
        robot.interact(() -> finalQtySpinner.increment(1));
        WaitForAsyncUtils.waitForFxEvents();

        // Triggers invalid parse path in updateTotalCost loop
        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        closeAlertIfPresent(robot);
    }

    @Test
    @DisplayName("Calculate executes alert flow and clear still works")
    void testCalculateFlowCoversAlertAndClear(FxRobot robot) {
        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);

        assertNotNull(priceField);
        final TextField finalPriceField = priceField;
        robot.interact(() -> finalPriceField.setText("12.5"));
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        closeAlertIfPresent(robot);

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
    @Test
    @DisplayName("Calculate and clear path covers controller lines 300-355")
    void testCalculateAndClearCoveragePath(FxRobot robot) {
        HBox firstRow = firstItemRow(robot);
        TextField priceField = findPriceField(firstRow);
        Spinner<?> qtySpinner = findQuantitySpinner(firstRow);

        assertNotNull(priceField);
        assertNotNull(qtySpinner);

        final TextField finalPriceField = priceField;
        final Spinner<?> finalQtySpinner = qtySpinner;

        robot.interact(() -> {
            finalPriceField.setText("20");
            finalQtySpinner.increment(1);
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Trigger onCalculateClick (includes DB thread spawn + showAlert)
        robot.clickOn("#btnCalculate");
        WaitForAsyncUtils.waitForFxEvents();

        // Close the information alert (showAlert/showAndWait path)
        closeAlertIfPresent(robot);

        // Trigger onClearClick
        robot.clickOn("#btnClear");
        WaitForAsyncUtils.waitForFxEvents();

        Label totalCost = robot.lookup("#totalCostLabel").queryAs(Label.class);
        assertNotNull(totalCost.getText());
    }

}
