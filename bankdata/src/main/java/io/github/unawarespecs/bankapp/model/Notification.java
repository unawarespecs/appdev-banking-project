package io.github.unawarespecs.bankapp.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {
    private int id;
    private int userID;
    private String type;
    private String status;
    private LocalDateTime created;

    public Notification() {}

    public Notification(int id, int userID, String type, String status, LocalDateTime created) {
        this.id = id;
        this.userID = userID;
        this.type = type;
        this.status = status;
        this.created = created;
    }

    public Notification( int userID, String type, String status) {
        this.userID = userID;
        this.type = type;
        this.status = status;
    }


}
