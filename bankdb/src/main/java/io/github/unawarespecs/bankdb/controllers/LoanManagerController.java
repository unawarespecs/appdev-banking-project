package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.entity.LoanData;
import io.github.unawarespecs.bankapp.model.Loan;
import io.github.unawarespecs.bankapp.model.LoanPlan;
import io.github.unawarespecs.bankapp.model.User;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.List;
import java.util.function.Consumer;

public class LoanManagerController {

    @javafx.fxml.FXML
    private TableColumn<Loan, String> colLoanAccountName;
    @javafx.fxml.FXML
    private TableView<Loan> loanAccountsTable;
    @javafx.fxml.FXML
    private TextField searchLoanField;
    @javafx.fxml.FXML
    private Button searchLoanBtn;
    @javafx.fxml.FXML
    private TableColumn<Loan, Integer> colLoanTerm;
    @javafx.fxml.FXML
    private TableColumn<Loan, Double> colLoanAmount;
    @javafx.fxml.FXML
    private Button savePlanBtn;
    @javafx.fxml.FXML
    private TableColumn<Loan, Integer> colLoanAccountId;
    @javafx.fxml.FXML
    private Button backBtn;
    @javafx.fxml.FXML
    private TableColumn<Loan, Double> colLoanRate;
    @javafx.fxml.FXML
    private TextField loanNameField;

    private final BankInterface bankService;
    @Setter
    private Consumer<Stage> onBackRequested;

    private javafx.collections.ObservableList<Loan> masterData = javafx.collections.FXCollections.observableArrayList();

    @javafx.fxml.FXML
    private Spinner<Integer> loanDurationField;
    @javafx.fxml.FXML
    private Spinner<Double> loanMaxLimitField;
    @javafx.fxml.FXML
    private Spinner<Double> loanInterestField;

    @javafx.fxml.FXML
    public void initialize() throws Exception {
        colLoanAccountId.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colLoanAmount.setCellValueFactory(new PropertyValueFactory<>("moneyLeftToRepay"));
        colLoanRate.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        colLoanTerm.setCellValueFactory(new PropertyValueFactory<>("duration"));

        colLoanAccountName.setCellValueFactory(new PropertyValueFactory<>("userID"));

        colLoanAmount.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : String.format("₱%.2f", amount));
            }
        });

        colLoanRate.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double rate, boolean empty) {
                super.updateItem(rate, empty);
                setText(empty || rate == null ? null : String.format("%.2f%%", rate));
            }
        });

        searchLoanField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleLoanSearch(newValue);
        });

        loanDurationField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, 6, 1));
        loanMaxLimitField.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(100.0, 1000000.0, 10000.0, 500.0));
        loanInterestField.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, 5.0, 0.25));

        loadData();
    }

    public LoanManagerController(BankInterface bankService) {
        this.bankService = bankService;
    }

    @javafx.fxml.FXML
    public void onBackClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        if (onBackRequested != null) {
            onBackRequested.accept(stage);
        }
    }

    @javafx.fxml.FXML
    public void onSavePlanClick(ActionEvent actionEvent) {
        String name = loanNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showError("Input Error", "Please provide a valid identifier description for this configuration.");
            return;
        }

        int duration = loanDurationField.getValue();
        double maxLimit = loanMaxLimitField.getValue();
        double interestRate = loanInterestField.getValue();

        LoanPlan lp = new LoanPlan(name, duration, maxLimit, interestRate);
        try {
            bankService.createLoanPlan(lp);
            showInformation("Loan Plan Created", "Plan configuration '" + name + "' updated successfully.");
        } catch (Exception e) {
            showError("Failed to Create Loan Plan", "Failed to compile transaction profile: " + e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void onSearchLoanClick(ActionEvent actionEvent) {
        handleLoanSearch(searchLoanField.getText());
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadData() throws Exception {
        List<Loan> loans = bankService.getAllActiveLoans();
        if (loans != null) {
            masterData.setAll(loans);
            loanAccountsTable.setItems(masterData);
        }
    }

    private void handleLoanSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            loanAccountsTable.setItems(masterData);
            return;
        }

        String lowerCaseFilter = query.toLowerCase().trim();
        ObservableList<Loan> filteredList = javafx.collections.FXCollections.observableArrayList();

        for (Loan loan : masterData) {
            String userIdStr = String.valueOf(loan.getUserID());

            if (userIdStr.contains(lowerCaseFilter)) {
                filteredList.add(loan);
            }
        }

        loanAccountsTable.setItems(filteredList);
    }
}