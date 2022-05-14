package com.example.ebankingbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transaction_record")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {
    @Id
    @Column(name = "transaction_id", nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID transactionId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "value_date", nullable = false)
    private LocalDateTime valueDate;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_iban_code", nullable = false, referencedColumnName = "iban_code")
    private Account accountIbanCode;

}
