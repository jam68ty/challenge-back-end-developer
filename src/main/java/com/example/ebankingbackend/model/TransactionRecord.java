package com.example.ebankingbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transaction_record")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord implements Serializable, Deserializer {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "transaction_id", nullable = false, columnDefinition = "BINARY(16)")
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

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "iban_code", nullable = false, referencedColumnName = "iban_code")
    private Account ibanCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "multi_currency_account_id", nullable = false, referencedColumnName = "multi_currency_account_id")
    private MultiCurrencyAccount multiCurrencyAccountId;

    @Override
    public Object deserialize(byte[] bytes) throws DeserializationException {
        return null;
    }
}
