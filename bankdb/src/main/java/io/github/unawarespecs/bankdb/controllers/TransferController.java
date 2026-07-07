package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@Deprecated
public class TransferController
{
    @javafx.fxml.FXML
    private TextField recipientField;
    @javafx.fxml.FXML
    private TextField amountField;

    BankInterface bankService;

    @javafx.fxml.FXML
    public void initialize() {
    }

    public TransferController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @javafx.fxml.FXML
    public void OnConfirmTransfer(ActionEvent actionEvent) throws Exception {
        String recipient = recipientField.getText();
        Double amount = Double.parseDouble(amountField.getText());

        bankService.transferMoney(bankService.getCurrentlyLoggedInCustomer(),null,amount);
    }
}