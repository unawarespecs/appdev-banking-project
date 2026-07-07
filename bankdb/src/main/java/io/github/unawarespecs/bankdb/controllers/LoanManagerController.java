package io.github.unawarespecs.bankdb.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class LoanManagerController
{
    @javafx.fxml.FXML
    private TextField loanDurationField;
    @javafx.fxml.FXML
    private TextField loanMaxLimitField;
    @javafx.fxml.FXML
    private TableColumn colLoanAccountName;
    @javafx.fxml.FXML
    private TableView loanAccountsTable;
    @javafx.fxml.FXML
    private TextField searchLoanField;
    @javafx.fxml.FXML
    private Button searchLoanBtn;
    @javafx.fxml.FXML
    private TableColumn colLoanTerm;
    @javafx.fxml.FXML
    private TableColumn colLoanAmount;
    @javafx.fxml.FXML
    private Button savePlanBtn;
    @javafx.fxml.FXML
    private TableColumn colLoanAccountId;
    @javafx.fxml.FXML
    private Button backBtn;
    @javafx.fxml.FXML
    private TextField loanInterestField;
    @javafx.fxml.FXML
    private TableColumn colLoanRate;

    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void onBackClick(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void onSavePlanClick(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void onSearchLoanClick(ActionEvent actionEvent) {
    }
}