package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MenuController {

    private final BankInterface bankService;

    public MenuController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @FXML
    void onDashboardClick(ActionEvent event) {
        Customer current = bankService.getCurrentlyLoggedInCustomer();
        if (current != null) {
            try {
                double balance = bankService.checkBalance(current);
                showInformation("Dashboard", "Welcome back, " + current.getUsername() + "!\nYour current balance is: $" + balance);
            } catch (Exception e) {
                showError("Error Fetching Balance", e.getMessage());
            }
        } else {
            showError("Session Error", "No active customer session found.");
        }
    }

    @FXML
    void onTransactionsClick(ActionEvent event) {
        System.out.println("Redirecting to transaction ledger...");
        // Future transaction
    }

    @FXML
    void onTransferClick(ActionEvent event) {
        System.out.println("Opening money transfer interface...");
        // Future logic to transfer
    }

    @FXML
    void onSettingsClick(ActionEvent event) {
        System.out.println("Opening account profile settings...");
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        bankService.setCurrentlyLoggedInCustomer(null);
        bankService.setCurrentlyLoggedInAdmin(null);

        showInformation("Logged Out", "You have successfully logged out of your session.");
        // Place view navigation logic here to reload your standard login FXML file
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}