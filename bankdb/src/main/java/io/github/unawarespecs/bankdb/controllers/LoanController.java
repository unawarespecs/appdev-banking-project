package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Transaction;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;

public class LoanController {
    private final BankInterface bankService;
    public LoanController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void handleLoanSubmit(ActionEvent actionEvent) {


        //if success
        Transaction t = new Transaction(bankService.getCurrentlyLoggedInCustomer().getId(), amount, "Loan");
        bankService.addTransaction(t);
    }
}
