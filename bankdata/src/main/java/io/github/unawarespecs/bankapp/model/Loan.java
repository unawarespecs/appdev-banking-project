package io.github.unawarespecs.bankapp.model;

import lombok.Data;

@Data
public class Loan {
    private int id;
    private int userID;
    private double loanMoney;
    private double moneyLeftToRepay;
    private int duration;
    private double interestRate;
    private double installmentRate;
}
