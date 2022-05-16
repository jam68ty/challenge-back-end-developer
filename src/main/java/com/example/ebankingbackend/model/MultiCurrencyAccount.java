package com.example.ebankingbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "multi_currency_account")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode
public class MultiCurrencyAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "multi_currency_account_serial", nullable = false)
    private long multiCurrencyAccountSerial;

    @Column(name = "currency", nullable = false)
    private String currency;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="iban_code", nullable=false, updatable = false)
    @JsonIgnore
    private Account ibanCode;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "balance", nullable = false)
    private double balance;

    @Column(name = "multi_currency_account_id", nullable = false)
    private String multiCurrencyAccountId;

    @PostPersist
    public void createMultiCurrencyAccountId() {
        this.multiCurrencyAccountId = "MCA" + String.format("%012d", multiCurrencyAccountSerial);
    }

}
