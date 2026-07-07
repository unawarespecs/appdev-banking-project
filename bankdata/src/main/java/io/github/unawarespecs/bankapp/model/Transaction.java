package io.github.unawarespecs.bankapp.model;

import lombok.Data;

@Data
public class Transaction {
    private int id;
    private int customerId;
    private String date;
    private String type;
    private double amount;
    private String status;

    public Transaction() {}

    public Transaction(int id, int customerId, String date, String type, double amount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }
}
