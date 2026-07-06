package io.github.unawarespecs.bankapp.jfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomerController {

    @FXML private Label balanceLabel;
    @FXML private TextField amountInput;

    @FXML
    private void handleDeposit(ActionEvent event) {
        balanceLabel.setText("Deposit Button Clicked");
    }

    @FXML
    private void handleWithdraw(ActionEvent event) {
        balanceLabel.setText("Withdraw Button Clicked");
    }
}