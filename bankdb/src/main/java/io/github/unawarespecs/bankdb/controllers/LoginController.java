package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @javafx.fxml.FXML
    private TextField usernameField;
    @javafx.fxml.FXML
    private PasswordField passwordField;
    @javafx.fxml.FXML
    private Label errorLabel;

    @javafx.fxml.FXML
    private VBox parentBox;

    private final BankInterface bankService;
    private Runnable onSuccessfulLogin;

    private Stage getStageFromVBox() {
        return (Stage) parentBox.getScene().getWindow();
    }

    public LoginController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void setOnSuccessfulLogin(Runnable onSuccessfulLogin) {
        this.onSuccessfulLogin = onSuccessfulLogin;
    }

    @javafx.fxml.FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @javafx.fxml.FXML
    public void onLoginButtonClick(ActionEvent actionEvent) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password fields cannot be empty.");
            return;
        }

        try {
            Customer[] customers = bankService.getCustomers();
            Customer matchingCustomer = null;

            for (Customer cust : customers) {
                //System.out.println(cust);
                if (cust.getUsername().equals(username) && cust.getPassword().equals(password)) {
                    matchingCustomer = cust;
                    break;
                }
            }
            //System.out.println(matchingCustomer);
            if (matchingCustomer != null) {
                if (matchingCustomer.isAccountFrozen()) {
                    errorLabel.setText("Access denied: Your account is frozen.");
                    return;
                }

                bankService.setCurrentlyLoggedInCustomer(matchingCustomer);
                errorLabel.setText("Login successful! Redirecting...");

                if (onSuccessfulLogin != null) {
                    onSuccessfulLogin.run();
                }
            } else {
                errorLabel.setText("Invalid username or password.");
            }

        } catch (Exception e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }
}