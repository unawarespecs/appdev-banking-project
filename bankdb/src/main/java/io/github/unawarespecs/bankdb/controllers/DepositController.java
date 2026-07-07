package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.service.BankInterface;

public class DepositController {
    private final BankInterface bankService;
    public DepositController(BankInterface bankService) {
        this.bankService = bankService;
    }
}
