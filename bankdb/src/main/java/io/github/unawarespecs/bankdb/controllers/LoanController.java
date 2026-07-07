package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.LoanPlan;
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

public class LoanController implements Initializable {
    private final BankInterface bankService;
    public Button applyLoanButton;

    @FXML
    private ComboBox<LoanPlan> loanPlansComboBox;

    @FXML
    private TextField loanAmountField;

    @FXML
    private Label creditScoreLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label interestRateLabel;

    @FXML
    private Label maxAmountLabel;

    @FXML
    private Label statusLabel;


    @FXML
    private VBox planDetailsBox;

    public LoanController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLoanPlans();
        loadCreditScore();
        setupLoanPlanListener();
    }

    private void loadLoanPlans() {
        try {
            List<LoanPlan> plans = bankService.getLoanPlans();
            loanPlansComboBox.getItems().addAll(plans);
            
            if (!plans.isEmpty()) {
                loanPlansComboBox.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showError("Error loading loan plans: " + e.getMessage());
        }
    }

    private void loadCreditScore() {
        try {
            Customer currentCustomer = bankService.getCurrentlyLoggedInCustomer();
            if (currentCustomer != null) {
                int creditScore = bankService.getCreditScore(currentCustomer);
                creditScoreLabel.setText(String.valueOf(creditScore));
            }
        } catch (Exception e) {
            showError("Error loading credit score: " + e.getMessage());
        }
    }

    private void setupLoanPlanListener() {
        loanPlansComboBox.setOnAction(event -> updatePlanDetails());
    }

    private void updatePlanDetails() {
        LoanPlan selectedPlan = loanPlansComboBox.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            durationLabel.setText(selectedPlan.getDuration() + " months");
            interestRateLabel.setText(String.format("%.2f%%", selectedPlan.getInterestRate()));
            maxAmountLabel.setText(String.format("$%.2f", selectedPlan.getMaxAmount()));
        } else {
            durationLabel.setText("-");
            interestRateLabel.setText("-");
            maxAmountLabel.setText("-");
        }
    }

    @FXML
    public void handleLoanSubmit(ActionEvent event) {
        try {
            // Validate inputs
            LoanPlan selectedPlan = loanPlansComboBox.getSelectionModel().getSelectedItem();
            if (selectedPlan == null) {
                showError("Please select a loan plan");
                return;
            }

            String amountText = loanAmountField.getText().trim();
            if (amountText.isEmpty()) {
                showError("Please enter a loan amount");
                return;
            }

            double loanAmount;
            try {
                loanAmount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                showError("Invalid amount format. Please enter a valid number");
                return;
            }

            if (loanAmount <= 0) {
                showError("Loan amount must be greater than 0");
                return;
            }

            if (loanAmount > selectedPlan.getMaxAmount()) {
                showError("Loan amount exceeds maximum of Php" + selectedPlan.getMaxAmount());
                return;
            }

            // Apply for loan
            Customer currentCustomer = bankService.getCurrentlyLoggedInCustomer();
            if (currentCustomer == null) {
                showError("No customer logged in");
                return;
            }

            bankService.applyForLoan(currentCustomer, selectedPlan, loanAmount);
            
            // Log transaction
            Transaction trans = new Transaction();
            trans.setType("LOAN_APPLICATION");
            trans.setAmount(loanAmount);
            trans.setCreated(LocalDateTime.now());
            trans.setCustID(currentCustomer.getId());
            bankService.addTransaction(trans);
            
            showSuccess("Loan application successful! Loan amount: Php" + String.format("%.2f", loanAmount));
            
            // Clear form
            loanAmountField.clear();
            statusLabel.setStyle("-fx-text-fill: #27ae60;");

        } catch (Exception e) {
            showError("Error applying for loan: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }
}
