package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.model.Transaction;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TransactionLedgerController {

    private final BankInterface bankService;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> colDate;

    @FXML
    private TableColumn<Transaction, String> colType;

    @FXML
    private TableColumn<Transaction, Double> colAmount;

    @FXML
    private TableColumn<Transaction, String> colStatus;

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    public TransactionLedgerController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAmount.setCellFactory(tc -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("₱%,.2f", amount));
                }
            }
        });

        transactionTable.setItems(transactionList);
        loadTransactionHistory();
    }

    private void loadTransactionHistory() {
        Customer current = bankService.getCurrentlyLoggedInCustomer();
        if (current == null) {
            showError("Session Error", "No active customer session found.");
            return;
        }

        try {
            List<Transaction> history = bankService.getTransactionHistory(current);
            transactionList.setAll(history);
        } catch (Exception e) {
            showError("Error Loading Transactions", "Could not fetch transaction history: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
