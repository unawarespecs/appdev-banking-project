package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.model.Transaction;
import io.github.unawarespecs.bankapp.service.BankInterface;
import io.github.unawarespecs.bankdb.utils.PINValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class TransactionController
{
    @javafx.fxml.FXML
    private TableView<Transaction> transactionTable;
    @javafx.fxml.FXML
    private TableColumn<Transaction, String> colType;
    @javafx.fxml.FXML
    private TableColumn<Transaction, Double> colAmt;
    @javafx.fxml.FXML
    private TableColumn<Transaction, LocalDateTime> colDate;
    @javafx.fxml.FXML
    private TableColumn<Transaction, String> colStatus;

    private final BankInterface bankService;
    @javafx.fxml.FXML
    private Button backBtn;
    @Setter
    private Consumer<Stage> onBackRequested;

    private boolean isPINValidated = false;

    @javafx.fxml.FXML
    public void initialize() throws Exception {

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("created"));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty("Success"));

        colAmt.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : String.format("₱%.2f", amount));
            }
        });

        colDate.setCellFactory(tc -> new TableCell<>() {
            private final DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : formatter.format(date));
            }
        });

        // Validate PIN before loading transaction data
        Customer currentCustomer = bankService.getCurrentlyLoggedInCustomer();
        if (currentCustomer != null && PINValidator.validatePIN(currentCustomer, "View Transaction History")) {
            isPINValidated = true;
            loadData();
        }
    }

    public TransactionController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void loadData() throws Exception {
        List<Transaction> trans = bankService.getTransactions(bankService.getCurrentlyLoggedInCustomer());
        transactionTable.getItems().setAll(trans);
    }

}