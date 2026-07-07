package io.github.unawarespecs.bankapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.unawarespecs.bankapp.model.Customer;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transaction_data")
public class TransactionData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private CustomerData customer;

    String type; //withdraw, transfer, deposit
    double amount;


    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime created;

}
