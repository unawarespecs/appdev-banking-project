package io.github.unawarespecs.bankdb.controllers;

import io.github.unawarespecs.bankapp.model.Notification;
import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.scene.control.ListView;

import java.util.List;

public class NotifController
{
    @javafx.fxml.FXML
    private ListView<Notification> notificationListView;
    private final BankInterface bankService;
    public NotifController(BankInterface bankService) {
        this.bankService = bankService;
    }
    @javafx.fxml.FXML
    public void initialize() {

        notificationListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Extracts and displays only the status message string
                    setText(item.getStatus());
                }
            }
        });
        List<Notification> trans = bankService.getNotifications(bankService.getCurrentlyLoggedInCustomer());
        notificationListView.getItems().setAll(trans);


    }
}