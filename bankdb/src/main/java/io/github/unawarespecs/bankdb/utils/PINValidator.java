package io.github.unawarespecs.bankdb.utils;

import io.github.unawarespecs.bankapp.model.Customer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Utility class for PIN validation dialogs in banking operations.
 */
public class PINValidator {

    /**
     * Shows a PIN validation dialog and returns true if the entered PIN matches the customer's PIN.
     *
     * @param customer The customer whose PIN needs to be validated
     * @param operationName The name of the operation (e.g., "Deposit", "Withdrawal")
     * @return true if PIN is correct, false otherwise or if cancelled
     */
    public static boolean validatePIN(Customer customer, String operationName) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("PIN Verification");
        dialogStage.setWidth(350);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11;");

        Label titleLabel = new Label("Enter PIN for " + operationName);
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label instructionLabel = new Label("Please enter your PIN to confirm this transaction.");
        instructionLabel.setStyle("-fx-text-fill: #7f8c8d;");
        instructionLabel.setWrapText(true);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");
        pinField.setPrefHeight(40);
        pinField.setStyle("-fx-padding: 10; -fx-font-size: 12; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setWrapText(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: CENTER_RIGHT;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(100);
        cancelBtn.setStyle("-fx-padding: 10; -fx-font-size: 11;");

        Button confirmBtn = new Button("Confirm");
        confirmBtn.setPrefWidth(100);
        confirmBtn.setStyle("-fx-padding: 10; -fx-font-size: 11; -fx-background-color: #2ecc71; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(cancelBtn, confirmBtn);

        root.getChildren().addAll(titleLabel, instructionLabel, pinField, errorLabel, buttonBox);

        final boolean[] isValidated = {false};

        confirmBtn.setOnAction(event -> {
            String enteredPin = pinField.getText().trim();
            if (enteredPin.isEmpty()) {
                errorLabel.setText("PIN cannot be empty.");
                return;
            }

            try {
                int enteredPinInt = Integer.parseInt(enteredPin);
                // Validate PIN
                if (customer.getPin() == enteredPinInt) {
                    isValidated[0] = true;
                    dialogStage.close();
                } else {
                    errorLabel.setText("Incorrect PIN. Please try again.");
                    pinField.clear();
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("PIN must be numeric.");
                pinField.clear();
            }
        });

        cancelBtn.setOnAction(event -> dialogStage.close());

        // Allow Enter key to submit
        pinField.setOnAction(event -> confirmBtn.fire());

        Scene scene = new Scene(root);
        scene.setUserAgentStylesheet(
                Objects.requireNonNull(
                        PINValidator.class.getResource("/assets/css/fluent-override.css")
                ).toExternalForm()
        );
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return isValidated[0];
    }
}

