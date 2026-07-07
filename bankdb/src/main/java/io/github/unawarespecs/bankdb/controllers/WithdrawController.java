package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.service.BankInterface;
import io.github.unawarespecs.bankdb.utils.PINValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class WithdrawController {

    private final BankInterface bankService;

    @FXML
    private TextField withdrawAmountField;

    @FXML
    private Label withdrawStatusLabel;

    public WithdrawController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @FXML
    private void handleWithdrawSubmit(ActionEvent event) {
        String amountText = withdrawAmountField.getText() == null ? "" : withdrawAmountField.getText().trim();
        if (amountText.isEmpty()) {
            showError("Validation Error", "Please enter an amount to withdraw.");
            return;
        }

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

        Customer current = bankService.getCurrentlyLoggedInCustomer();
        if (current == null) {
            showError("Session Error", "No active customer session found.");
            return;
        }

        // Validate PIN before proceeding
        if (!PINValidator.validatePIN(current, "Withdrawal")) {
            showError("PIN Verification Failed", "Incorrect PIN. Withdrawal cancelled.");
            return;
        }

        try {
            bankService.withdrawMoney(current, amount);
            String msg = String.format("Withdrew $%,.2f successfully.", amount);
            withdrawStatusLabel.setText(msg);
            showInformation("Withdrawal Successful", msg);
            withdrawAmountField.clear();
        } catch (Exception e) {
            showError("Withdrawal Failed", e.getMessage());
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
