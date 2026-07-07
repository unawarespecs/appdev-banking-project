package io.github.unawarespecs.bankapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "customer_data", options = "AUTO_INCREMENT=3")
public class CustomerData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    UUID uuid;
    String username;
    String password;
    double balance;
    int pin;
    boolean isAccountFrozen;
    int creditScore;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime created;

}
