package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.service.BankInterface;

public class WithdrawController {
    private final BankInterface bankService;
    public WithdrawController(BankInterface bankService) {
        this.bankService = bankService;
    }
}
