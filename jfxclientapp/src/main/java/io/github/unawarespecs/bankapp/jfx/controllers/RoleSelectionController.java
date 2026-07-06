package io.github.unawarespecs.bankapp.jfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RoleSelectionController {

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        switchScene(event, "/io/github/unawarespecs/bankapp/jfx/controllers/AdminDashboard.fxml", "Admin Dashboard");
    }

    @FXML
    private void handleCustomerLogin(ActionEvent event) {
        switchScene(event, "/io/github/unawarespecs/bankapp/jfx/controllers/CustomerDashboard.fxml", "Customer Dashboard");
    }

    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load the FXML file. Check the path!");
        }
    }
}