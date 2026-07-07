package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Loan;
import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.model.Transaction;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class PayLoanController implements Initializable {
    private final BankInterface bankService;
    public Button payLoanButton;

    @FXML
    private ComboBox<Loan> activeLoansComboBox;

    @FXML
    private TextField paymentAmountField;

    @FXML
    private Label loanAmountLabel;

    @FXML
    private Label remainingLabel;

    @FXML
    private Label loanInterestRateLabel;

    @FXML
    private Label paymentStatusLabel;

    @FXML
    private VBox loanDetailsBox;

    public PayLoanController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadActiveLoans();
        setupLoanSelectionListener();
    }

    private void loadActiveLoans() {
        try {
            Customer currentCustomer = bankService.getCurrentlyLoggedInCustomer();
            if (currentCustomer != null) {
                List<Loan> loans = bankService.getLoans(currentCustomer);
                activeLoansComboBox.getItems().addAll(loans);

                if (!loans.isEmpty()) {
                    activeLoansComboBox.getSelectionModel().selectFirst();
                } else {
                    showError("No active loans found");
                }
            } else {
                showError("No customer logged in");
            }
        } catch (Exception e) {
            showError("Error loading loans: " + e.getMessage());
        }
    }

    private void setupLoanSelectionListener() {
        activeLoansComboBox.setOnAction(event -> updateLoanDetails());
    }

    private void updateLoanDetails() {
        Loan selectedLoan = activeLoansComboBox.getSelectionModel().getSelectedItem();
        if (selectedLoan != null) {
            loanAmountLabel.setText(String.format("$%.2f", selectedLoan.getLoanMoney()));
            remainingLabel.setText(String.format("$%.2f", selectedLoan.getMoneyLeftToRepay()));
            loanInterestRateLabel.setText(String.format("%.2f%%", selectedLoan.getInterestRate()));
        } else {
            loanAmountLabel.setText("-");
            remainingLabel.setText("-");
            loanInterestRateLabel.setText("-");
        }
    }

    @FXML
    public void payLoanSubmit(ActionEvent event) {
        try {
            // Validate inputs
            Loan selectedLoan = activeLoansComboBox.getSelectionModel().getSelectedItem();
            if (selectedLoan == null) {
                showError("Please select a loan to pay");
                return;
            }

            String amountText = paymentAmountField.getText().trim();
            if (amountText.isEmpty()) {
                showError("Please enter a payment amount");
                return;
            }

            double paymentAmount;
            try {
                paymentAmount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                showError("Invalid amount format. Please enter a valid number");
                return;
            }

            if (paymentAmount <= 0) {
                showError("Payment amount must be greater than 0");
                return;
            }

            if (paymentAmount > selectedLoan.getMoneyLeftToRepay()) {
                showError("Payment amount exceeds remaining balance of Php " + String.format("%.2f", selectedLoan.getMoneyLeftToRepay()));
                return;
            }

            // Make payment
            Customer currentCustomer = bankService.getCurrentlyLoggedInCustomer();
            if (currentCustomer == null) {
                showError("No customer logged in");
                return;
            }

            bankService.payLoan(currentCustomer, selectedLoan, paymentAmount);

            // Log transaction
            Transaction trans = new Transaction();
            trans.setType("LOAN_PAYMENT");
            trans.setAmount(paymentAmount);
            trans.setCreated(LocalDateTime.now());
            trans.setCustID(currentCustomer.getId());
            bankService.addTransaction(trans);

            showSuccess("Payment successful! Amount paid: Php " + String.format("%.2f", paymentAmount));

            // Refresh loans and clear form
            activeLoansComboBox.getItems().clear();
            paymentAmountField.clear();
            loadActiveLoans();

        } catch (Exception e) {
            showError("Error making payment: " + e.getMessage());
        }
    }

    private void showError(String message) {
        paymentStatusLabel.setText(message);
        paymentStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        paymentStatusLabel.setText(message);
        paymentStatusLabel.setStyle("-fx-text-fill: #27ae60;");
    }
}
