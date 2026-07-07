package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * Controller for the customer settings screen (profile, update PIN/password, delete account).
 */
public class CustomerSettingsController {

    private final BankInterface bankService;
    private Runnable onDeleteComplete;

    @FXML
    private Label accNumLabel;

    @FXML
    private Label accNameLabel;

    @FXML
    private Label accTypeLabel;

    @FXML
    private Label accValidityLabel;

    @FXML
    private PasswordField oldPinField;

    @FXML
    private PasswordField newPinField;

    @FXML
    private PasswordField confirmPinField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    public CustomerSettingsController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void setOnDeleteComplete(Runnable onDeleteComplete) {
        this.onDeleteComplete = onDeleteComplete;
    }

    @FXML
    void initialize() {
        // Populate account details
        Customer current = bankService.getCurrentlyLoggedInCustomer();
        if (current != null) {
            try {
                int accNum = bankService.getAccountNumber(current);
                accNumLabel.setText(String.valueOf(accNum));
            } catch (Exception e) {
                accNumLabel.setText("N/A");
            }
            accNameLabel.setText(current.getUsername());
            // leave card type and validity as-is in the FXML or set defaults
            if (accTypeLabel.getText() == null || accTypeLabel.getText().isEmpty()) {
                accTypeLabel.setText("Debit Card");
            }
            if (accValidityLabel.getText() == null || accValidityLabel.getText().isEmpty()) {
                accValidityLabel.setText("12/29");
            }
        } else {
            accNumLabel.setText("N/A");
            accNameLabel.setText("Guest");
        }
    }

    @FXML
    void onSettingsClick(ActionEvent event) {
        // Determine which settings operation (button text)
        String sourceText = "";
        try {
            sourceText = ((javafx.scene.control.Button) event.getSource()).getText();
        } catch (Exception ignored) {
        }

        Customer current = bankService.getCurrentlyLoggedInCustomer();
        if (current == null) {
            showError("Session Error", "No active customer session found.");
            return;
        }

        if (sourceText.toLowerCase().contains("pin")) {
            // Update PIN flow
            String oldPin = oldPinField.getText().trim();
            String newPin = newPinField.getText().trim();
            String confirmPin = confirmPinField.getText().trim();

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                showError("Validation Error", "Please fill all PIN fields.");
                return;
            }

            int oldPinVal;
            int newPinVal;
            try {
                oldPinVal = Integer.parseInt(oldPin);
                newPinVal = Integer.parseInt(newPin);
            } catch (NumberFormatException e) {
                showError("Validation Error", "PIN must be numeric.");
                return;
            }

            if (oldPinVal != current.getPin()) {
                showError("Validation Error", "Old PIN is incorrect.");
                return;
            }

            if (!newPin.equals(confirmPin)) {
                showError("Validation Error", "New PIN and confirmation do not match.");
                return;
            }

            // Basic PIN length check (common 4 digits)
            if (newPin.length() < 4) {
                showError("Validation Error", "PIN must be at least 4 digits.");
                return;
            }

            // Persist
            try {
                current.setPin(newPinVal);
                bankService.updateAccount(current);
                showInformation("Success", "PIN updated successfully.");
                oldPinField.clear(); newPinField.clear(); confirmPinField.clear();
            } catch (Exception e) {
                showError("Update Failed", e.getMessage());
            }

        } else if (sourceText.toLowerCase().contains("password")) {
            // Update password flow
            String oldPass = oldPasswordField.getText().trim();
            String newPass = newPasswordField.getText().trim();
            String confirmPass = confirmPasswordField.getText().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showError("Validation Error", "Please fill all password fields.");
                return;
            }

            if (!oldPass.equals(current.getPassword())) {
                showError("Validation Error", "Old password is incorrect.");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                showError("Validation Error", "New password and confirmation do not match.");
                return;
            }

            if (newPass.length() < 6) {
                showError("Validation Error", "Password must be at least 6 characters.");
                return;
            }

            try {
                current.setPassword(newPass);
                bankService.updateAccount(current);
                showInformation("Success", "Password updated successfully.");
                oldPasswordField.clear(); newPasswordField.clear(); confirmPasswordField.clear();
            } catch (Exception e) {
                showError("Update Failed", e.getMessage());
            }
        }
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        // Used by the "Delete Account" button in the settings screen.
        Customer current = bankService.getCurrentlyLoggedInCustomer();
        if (current == null) {
            showError("Session Error", "No active customer session found.");
            return;
        }

        try {
            bankService.deleteAccount(current);
            bankService.setCurrentlyLoggedInCustomer(null);

            // If a callback was provided (SceneUtils will attach one), run it. Otherwise close this window.
            if (onDeleteComplete != null) {
                onDeleteComplete.run();
            } else {
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            showError("Delete Failed", e.getMessage());
        }
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
