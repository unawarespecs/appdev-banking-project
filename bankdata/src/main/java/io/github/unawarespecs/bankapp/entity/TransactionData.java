package io.github.unawarespecs.bankapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    // Map the database's last_updated column. Some schemas expect this to be non-null on insert;
    // annotate with @UpdateTimestamp so Hibernate will populate it on insert/update.
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

}
