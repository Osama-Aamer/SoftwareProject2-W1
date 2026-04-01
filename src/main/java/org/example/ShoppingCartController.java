package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.service.LocalizationService;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ShoppingCartController implements Initializable {

    @FXML
    private BorderPane rootVBox;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblLanguage;
    @FXML
    private Label lblItemCount;
    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<String> languageCombo;
    @FXML
    private Spinner<Integer> itemCountSpinner;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox itemsPanel;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Button btnCalculate;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnExit;

    private Locale currentLocale = new Locale("en", "US");
    private Map<String, String> localizedStrings;
    private ShoppingCart cart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cart = new ShoppingCart();

        languageCombo.getItems().addAll(
                "English (en_US)",
                "Finnish (fi_FI)",
                "Swedish (sv_SE)",
                "Japanese (ja_JP)",
                "Arabic (ar_SA)"
        );
        languageCombo.setValue("English (en_US)");
        languageCombo.setOnAction(e -> onLanguageChange());

        itemCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        itemCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateItemRows());

        // Set initial language
        setLanguage(currentLocale);

        btnCalculate.setOnAction(e -> onCalculateClick());
        btnClear.setOnAction(e -> onClearClick());
        btnExit.setOnAction(e -> System.exit(0));
    }

    /**
     * Handle language combo box changes
     */
    private void onLanguageChange() {
        String selection = languageCombo.getValue();
        currentLocale = switch (selection) {
            case "English (en_US)" -> new Locale("en", "US");
            case "Finnish (fi_FI)" -> new Locale("fi", "FI");
            case "Swedish (sv_SE)" -> new Locale("sv", "SE");
            case "Japanese (ja_JP)" -> new Locale("ja", "JP");
            case "Arabic (ar_SA)" -> new Locale("ar", "SA");
            default -> new Locale("en", "US");
        };

        setLanguage(currentLocale);
    }

    /**
     * Set the application language
     */
    private void setLanguage(Locale locale) {
        currentLocale = locale;
        totalCostLabel.setText("0.00");

        localizedStrings = LocalizationService.getLocalizedStrings(locale);

        // Update all UI text
        lblTitle.setText(localizedStrings.getOrDefault("app.title", "Shopping Cart"));
        lblLanguage.setText(localizedStrings.getOrDefault("language.select", "Select Language:"));
        lblItemCount.setText(localizedStrings.getOrDefault("items.prompt", "Enter number of items:"));
        lblTotal.setText(localizedStrings.getOrDefault("total.cost", "Total Cost:"));
        btnCalculate.setText(localizedStrings.getOrDefault("button.calculate", "Calculate"));
        btnClear.setText(localizedStrings.getOrDefault("button.clear", "Clear"));
        btnExit.setText(localizedStrings.getOrDefault("button.exit", "Exit"));

        // Apply text direction based on language
        applyTextDirection(locale);
    }


    private void applyTextDirection(Locale locale) {

        String lang = locale.getLanguage();
        boolean isRTL = lang.equals("ar") || lang.equals("fa") || lang.equals("ur") || lang.equals("he");

        Platform.runLater(() -> {
            if (rootVBox != null) {
                rootVBox.setNodeOrientation(
                        isRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT
                );
            }
        });
    }

    /**
     * Update item rows based on spinner value
     */
    private void updateItemRows() {
        int numItems = itemCountSpinner.getValue();
        itemsPanel.getChildren().clear();

        for (int i = 0; i < numItems; i++) {
            HBox itemRow = createItemRow(i + 1);
            itemsPanel.getChildren().add(itemRow);
        }
    }


    private HBox createItemRow(int itemNumber) {
        HBox row = new HBox(10);
        row.setPadding(new javafx.geometry.Insets(10));
        row.setStyle("-fx-border-color: #3498db; -fx-border-radius: 8; -fx-background-color: linear-gradient(to right, #f8f9fa, #ffffff); -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 1, 1);");

        Label itemLabel = new Label(
                localizedStrings.getOrDefault("item.label", "Item") + " " + itemNumber
        );
        itemLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2c3e50;");

        Label priceLabel = new Label(localizedStrings.getOrDefault("item.price", "Price:"));
        priceLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: #2c3e50;");
        javafx.scene.control.TextField priceField = new javafx.scene.control.TextField();
        priceField.setPrefWidth(80);
        priceField.setPromptText("0.00");
        priceField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #95a5a6; -fx-border-width: 2; -fx-padding: 5px; -fx-border-radius: 4;");

        Label quantityLabel = new Label(localizedStrings.getOrDefault("item.quantity", "Quantity:"));
        quantityLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: #2c3e50;");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setPrefWidth(80);

        Label itemTotalLabel = new Label("0.00");
        itemTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #27ae60;");

        // Update when price or quantity changes
        priceField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double price = Double.parseDouble(newVal.isEmpty() ? "0" : newVal);
                int quantity = quantitySpinner.getValue();
                double itemTotal = price * quantity;
                itemTotalLabel.setText(String.format("%.2f", itemTotal));
                updateTotalCost();
            } catch (NumberFormatException ignored) {
            }
        });

        quantitySpinner.valueProperty().addListener((obs, old, newVal) -> {
            try {
                double price = Double.parseDouble(priceField.getText().isEmpty() ? "0" : priceField.getText());
                int quantity = newVal;
                double itemTotal = price * quantity;
                itemTotalLabel.setText(String.format("%.2f", itemTotal));
                updateTotalCost();
            } catch (NumberFormatException ignored) {
            }
        });

        // Store references
        row.setUserData(new Object[]{priceField, quantitySpinner});

        row.getChildren().addAll(
                itemLabel,
                new javafx.scene.control.Separator(javafx.geometry.Orientation.VERTICAL),
                priceLabel,
                priceField,
                quantityLabel,
                quantitySpinner,
                new Label(localizedStrings.getOrDefault("item.total", "Total:")),
                itemTotalLabel
        );

        return row;
    }


    private void updateTotalCost() {
        double total = 0;
        for (javafx.scene.Node node : itemsPanel.getChildren()) {
            if (node instanceof HBox row) {
                Object[] components = (Object[]) row.getUserData();
                if (components != null) {
                    javafx.scene.control.TextField priceField = (javafx.scene.control.TextField) components[0];
                    Spinner<Integer> quantitySpinner = (Spinner<Integer>) components[1];

                    try {
                        double price = Double.parseDouble(priceField.getText().isEmpty() ? "0" : priceField.getText());
                        int quantity = quantitySpinner.getValue();
                        total += price * quantity;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        totalCostLabel.setText(String.format("%.2f", total));
    }

    private void onCalculateClick() {
        updateTotalCost();
        showAlert("Success", localizedStrings.getOrDefault("calculation.complete", "Calculation completed!"));
    }


    private void onClearClick() {
        itemCountSpinner.getValueFactory().setValue(1);
        itemsPanel.getChildren().clear();
        totalCostLabel.setText("0.00");
        cart.clear();
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

