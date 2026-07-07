package io.github.unawarespecs.bankapp.jfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AdminController {

    @FXML private TextField nameInput;
    @FXML private TextField initialDepositInput;
    @FXML private Label statusLabel;

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        statusLabel.setText("Create Account Button Clicked");
    }

    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        statusLabel.setText("Delete Account Button Clicked");
    }

    @FXML
    private void handleModifyAccount(ActionEvent event) {
        statusLabel.setText("Modify Account Button Clicked");
    }
}
