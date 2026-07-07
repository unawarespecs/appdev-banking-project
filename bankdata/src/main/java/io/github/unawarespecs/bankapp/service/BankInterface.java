package io.github.unawarespecs.bankapp.service;

import io.github.unawarespecs.bankapp.model.*;

import java.util.List;

public interface BankInterface {
    int getAccountNumber(Customer cust) throws NumberFormatException;

    String getOwner(Customer cust) throws Exception;

    Customer getCurrentlyLoggedInCustomer();

    void setCurrentlyLoggedInCustomer(Customer cust);

    Administrator getCurrentlyLoggedInAdmin();

    void setCurrentlyLoggedInAdmin(Administrator admin);

    User[] getAllUsers() throws Exception;

    // admin operations
    Customer[] getCustomers() throws Exception;

    void createAccount(User user, String type) throws Exception;

    User updateAccount(User user) throws Exception;

    void deleteAccount(User user) throws Exception;

    Administrator getAdminAccount(Integer id) throws Exception;

    Customer getAccount(Integer id) throws Exception;

    Administrator[] getAdmins() throws Exception;

    Administrator createAdminAccount(Administrator admin) throws Exception;

    Administrator updateAdminAccount(Administrator admin) throws Exception;

    // customer facing info
    double checkBalance(Customer cust) throws Exception;

    void depositMoney(Customer cust, double amount) throws Exception;

    void withdrawMoney(Customer cust, double amount) throws Exception;

    void transferMoney(Customer source, Customer destination, double amt) throws Exception;

    // loan stuff
    List<LoanPlan> getLoanPlans() throws Exception;

    void createLoanPlan(LoanPlan plan) throws Exception;

    void deleteLoanPlan(int planId) throws Exception;

    List<Loan> getLoans(Customer cust) throws Exception;

    void applyForLoan(Customer cust, LoanPlan plan, double amount) throws Exception;

    void payLoan(Customer cust, Loan loan, double amount) throws Exception;

    int getCreditScore(Customer cust) throws Exception;

    void updateCreditScore(Customer cust, int score) throws Exception;

    List<Loan> getAllActiveLoans() throws Exception;

    List<Loan> searchActiveLoans(String query) throws Exception;

    List<Transaction> getTransactions(Customer currentlyLoggedInCustomer);

    void addTransaction(Transaction trans);
    void deleteTransaction(Transaction trans);
}
