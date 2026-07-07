package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class TransferFundsController {

    private final BankInterface bankService;

    @FXML
    private TextField recipientField;

    @FXML
    private TextField amountField;

    // --- dinagdag ni zaf ---
    @FXML
    private javafx.scene.layout.VBox pinPopupOverlay;
    @FXML
    private javafx.scene.control.PasswordField pinEntryField;
    // -------------------------------

    @Setter
    private Runnable onTransferComplete;

    public TransferFundsController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @FXML
    void onConfirmTransferClick(ActionEvent event) {
        String recipientAccountId = recipientField.getText().trim();
        String amountText = amountField.getText().trim();

        // Validate input fields (Original code)
        if (recipientAccountId.isEmpty()) {
            showError("Validation Error", "Please enter a recipient account ID.");
            return;
        }

        if (amountText.isEmpty()) {
            showError("Validation Error", "Please enter an amount to transfer.");
            return;
        }

        // Validate amount (Original code)
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showError("Validation Error", "Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Amount must be a valid number.");
            return;
        }

        // PAGKATAPOS NG VALIDATION, LALABAS ANG PIN POP-UP (Hindi pa nagta-transfer)
        pinPopupOverlay.setVisible(true);
    }

    // --- dinagdag ni zaf---
    @FXML
    void onCancelPinClick(ActionEvent event) {
        pinPopupOverlay.setVisible(false);
        pinEntryField.clear();
    }

    @FXML
    void onSubmitPinClick(ActionEvent event) {
        String enteredPin = pinEntryField.getText().trim();

        if (enteredPin.isEmpty()) {
            showError("Validation Error", "Please enter your PIN.");
            return;
        }


        String recipientAccountId = recipientField.getText().trim();
        double amount = Double.parseDouble(amountField.getText().trim());

        // Get current logged-in customer
        Customer sender = bankService.getCurrentlyLoggedInCustomer();
        if (sender == null) {
            showError("Session Error", "No active customer session found.");
            pinPopupOverlay.setVisible(false);
            return;
        }

        // Get recipient customer by account ID
        Customer recipient;
        try {
            int recipientId = Integer.parseInt(recipientAccountId);
            recipient = bankService.getAccount(recipientId);
            if (recipient == null) {
                showError("Transfer Error", "Recipient account not found.");
                pinPopupOverlay.setVisible(false);
                return;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Recipient account ID must be a valid number.");
            pinPopupOverlay.setVisible(false);
            return;
        } catch (Exception e) {
            showError("Transfer Error", "Error retrieving recipient account: " + e.getMessage());
            pinPopupOverlay.setVisible(false);
            return;
        }

        // Attempt transfer
        try {
            bankService.transferMoney(sender, recipient, amount);
            showInformation("Transfer Successful", "Successfully transferred Php " + amount + " to account " + recipient.getUsername());

            // Clear fields
            recipientField.clear();
            amountField.clear();
            pinEntryField.clear();

            // Close the transfer window
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

            // Notify completion
            if (onTransferComplete != null) {
                onTransferComplete.run();
            }
        } catch (IllegalArgumentException e) {
            showError("Transfer Failed", e.getMessage());
        } catch (Exception e) {
            showError("Transfer Error", "An error occurred during transfer: " + e.getMessage());
        } finally {
            pinPopupOverlay.setVisible(false);
        }
    }


    private void showInformation(String title, String message) {
        // (Original code)
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        // (Original code)
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

