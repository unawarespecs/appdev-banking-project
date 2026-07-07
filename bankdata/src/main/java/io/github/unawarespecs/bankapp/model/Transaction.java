package io.github.unawarespecs.bankapp.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Transaction {

    private int id;
    private int custID;
    private double amount;
    private String type;
    private LocalDateTime created;

    public Transaction() {}

    public Transaction(int id, int custID, double amount, String type, LocalDateTime created) {
        this.id = id;
        this.custID = custID;
        this.amount = amount;
        this.type = type;
        this.created = created;
    }
    public Transaction(int custID, double amount, String type) {
        this.custID = custID;
        this.amount = amount;
        this.type = type;
    }
}
