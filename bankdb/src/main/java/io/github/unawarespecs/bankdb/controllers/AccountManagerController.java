package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Administrator;
import io.github.unawarespecs.bankapp.model.Customer;
import io.github.unawarespecs.bankapp.model.User;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AccountManagerController
{
    @javafx.fxml.FXML
    private ComboBox createRoleBox;
    @javafx.fxml.FXML
    private TableColumn<User, String> colName;
    @javafx.fxml.FXML
    private TextField createNameField;
    @javafx.fxml.FXML
    private TextField searchField;
    @javafx.fxml.FXML
    private Button searchBtn;
    @javafx.fxml.FXML
    private Button saveDetailsBtn;
    @javafx.fxml.FXML
    private TableView<User> accountTable;
    @javafx.fxml.FXML
    private TableColumn<User, Double> colBalance;
    @javafx.fxml.FXML
    private Button deleteBtn;
    @javafx.fxml.FXML
    private TextField createPasswordField;
    @javafx.fxml.FXML
    private Button backBtn;
    @javafx.fxml.FXML
    private TableColumn<User, Integer> colId;
    @javafx.fxml.FXML
    private TableColumn<User, String> colRole;
    @javafx.fxml.FXML
    private TableColumn<User, String> colStatus;

    @javafx.fxml.FXML
    private Button createBtn;

    private final BankInterface bankService;
    private Consumer<Stage> onBackRequested;

    private javafx.collections.ObservableList<User> masterData = javafx.collections.FXCollections.observableArrayList();

    @javafx.fxml.FXML
    private HBox actionStrip;

    @javafx.fxml.FXML
    public void initialize() throws Exception {

        accountTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colBalance.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user instanceof Customer) {
                return new SimpleObjectProperty<>(((Customer) user).getBalance());
            }
            return new SimpleObjectProperty<>(0.0); // Admins don't have balance
        });

        colStatus.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user instanceof Customer) {
                boolean isFrozen = ((Customer) user).isAccountFrozen();
                return new SimpleStringProperty(isFrozen ? "Frozen" : "Active");
            }
            return new SimpleStringProperty("Active"); // Admins cannot be frozen
        });

        createRoleBox.setItems(FXCollections.observableArrayList("Customer", "Admin"));



        //editable Rows
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            event.getRowValue().setUsername(event.getNewValue());
        });

        colRole.setCellFactory(ChoiceBoxTableCell.forTableColumn("Customer", "Admin"));
        colRole.setOnEditCommit(event -> {
            event.getRowValue().setRole(event.getNewValue());
        });
        colBalance.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));

        colBalance.setOnEditCommit(event -> {
            User user = event.getRowValue();

            if (user instanceof Customer) {
                ((Customer) user).setBalance(event.getNewValue());
            } else {
                accountTable.refresh();
            }
        });

        colStatus.setCellFactory(ChoiceBoxTableCell.forTableColumn("Frozen", "Active"));
        colStatus.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newStatus = event.getNewValue();

            if (user instanceof Customer) {
                boolean isFrozen = "Frozen".equalsIgnoreCase(newStatus);
                ((Customer) user).setAccountFrozen(isFrozen);
            }
        });

        actionStrip.visibleProperty().bind(
            accountTable.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0)
        );
        actionStrip.managedProperty().bind(actionStrip.visibleProperty());

        loadData();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });
    }

    public AccountManagerController(BankInterface bankService) {
        this.bankService = bankService;
    }

    public void setOnBackRequested(Consumer<Stage> onBackRequested) {
        this.onBackRequested = onBackRequested;
    }

    @javafx.fxml.FXML
    public void onBackClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        if (onBackRequested !=null){
            onBackRequested.accept(stage);
        }
    }

    @javafx.fxml.FXML
    public void onSaveDetailsClick(ActionEvent actionEvent) {
        List<User> modifiedUsers = new ArrayList<>(accountTable.getItems());

        if (modifiedUsers.isEmpty()) {
            showError("Save Error","No accounts available to save.");
            return;
        }

        try {
            for (User user : modifiedUsers) {
                bankService.updateAccount(user);
            }

            loadData();
            showInformation("Successful Save", "All changes successfully persisted to Database.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed save","Failed to save changes to the database.");
        }
    }

    @javafx.fxml.FXML
    public void onDeleteClick(ActionEvent actionEvent) {
        List<User> selectedUsers = new ArrayList<>(accountTable.getSelectionModel().getSelectedItems());

        if (selectedUsers.isEmpty()) {
            showError("Deletion Error", "No accounts selected");
            return;
        }

        try{
            for(User user : selectedUsers){
                bankService.deleteAccount(user);
                accountTable.getItems().remove(user);
            }
            accountTable.getSelectionModel().clearSelection();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @javafx.fxml.FXML
    public void onSearchClick(ActionEvent actionEvent) {
        String text = searchField.getText();
        handleSearch(text);
    }

    @javafx.fxml.FXML
    public void onCreateClick(ActionEvent actionEvent) throws Exception {
        String selectedRole = createRoleBox.getValue().toString();
        if (selectedRole == null || selectedRole.trim().isEmpty()) {
            showError("Creation Error","Please select a role before creating an account.");
            return;
        }
        User user = new User();
        user.setUsername(createNameField.getText());
        user.setPassword(createPasswordField.getText());
        System.out.println(user);
        bankService.createAccount(user, selectedRole.toLowerCase());
        loadData();
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

    private void loadData() throws Exception {
        User[] users = bankService.getAllUsers();

        if (users != null) {
            System.out.println(Arrays.toString(users));
            masterData.setAll(users);
            accountTable.setItems(FXCollections.observableArrayList(users));
        }
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            accountTable.setItems(masterData);
            return;
        }

        String lowerCaseFilter = query.toLowerCase().trim();
        javafx.collections.ObservableList<User> filteredList = javafx.collections.FXCollections.observableArrayList();

        for (User user : masterData) {
            String idStr = String.valueOf(user.getId());
            String name = user.getUsername() != null ? user.getUsername().toLowerCase() : "";

            if (idStr.contains(lowerCaseFilter) || name.contains(lowerCaseFilter)) {
                filteredList.add(user);
            }
        }

        accountTable.setItems(filteredList);
    }

}