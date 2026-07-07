package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;

public class PayLoanController {
    private final BankInterface bankService;
    public PayLoanController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void payLoanSubmit(ActionEvent actionEvent) {
    }
}
