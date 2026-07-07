package io.github.unawarespecs.bankapp.model;

import lombok.Data;

@Data
public class LoanPlan {
    private int id;
    private String name;
    private int duration;
    private double maxAmount;
    private double interestRate;

    public LoanPlan() {}

    public LoanPlan(int id, String name, int duration, double maxAmount, double interestRate) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
    }

    public LoanPlan(String name, int duration, double maxAmount, double interestRate) {
        this.name = name;
        this.duration = duration;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
    }
}
