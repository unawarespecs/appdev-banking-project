package io.github.unawarespecs.bankapp.jfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerController {

    @FXML private Label balanceLabel;
    @FXML private TextField amountInput;

    @FXML private TextField depositAmountField;
    @FXML private Button confirmDepositButton;
    @FXML private Label depositStatusLabel;

    @FXML private TextField withdrawAmountField;
    @FXML private Button confirmWithdrawButton;
    @FXML private Label withdrawStatusLabel;

    @FXML
    private void handleDepositSubmit(ActionEvent event) {
        if (depositStatusLabel != null) {
            depositStatusLabel.setText("Deposit of $" + depositAmountField.getText() + " submitted!");
        }
    }

    @FXML
    private void handleWithdrawSubmit(ActionEvent event) {
        if (withdrawStatusLabel != null) {
            withdrawStatusLabel.setText("Withdrawal of $" + withdrawAmountField.getText() + " submitted!");
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        System.out.println("Redirecting to dashboard...");
    }
}