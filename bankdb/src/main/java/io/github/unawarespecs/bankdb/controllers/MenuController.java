package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.function.Consumer;

public class MenuController {

    private final BankInterface bankService;
    private Consumer<Stage> onLogoutRequested;
    @FXML
    private VBox parentStage;

    @Setter
    private Consumer<Stage> onTransferFundsRequested;

    public MenuController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void setOnLogoutRequested(Consumer<Stage> onLogoutRequested) {
        this.onLogoutRequested = onLogoutRequested;
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


    }

    @FXML
    void onTransferClick(ActionEvent event) {
        System.out.println("Opening money transfer interface...");

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        if (onTransferFundsRequested !=null){
            onTransferFundsRequested.accept(stage);
        }
    }

    @FXML
    void onSettingsClick(ActionEvent event) {
        System.out.println("Opening account profile settings...");
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        bankService.setCurrentlyLoggedInCustomer(null);

        showInformation("Logged Out", "You have successfully logged out of your session.");
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        if (onLogoutRequested !=null){
            onLogoutRequested.accept(stage);
        }
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