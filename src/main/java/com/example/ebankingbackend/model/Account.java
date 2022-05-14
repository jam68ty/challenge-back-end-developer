package com.example.ebankingbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Table(name = "account")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "iban_code", nullable = false)
    private String ibanCode;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="user_id", nullable=false, referencedColumnName = "user_id", updatable = false)
    private User userId;

    @OneToMany(mappedBy = "accountIbanCode", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<MultiCurrencyAccount> multiCurrencyAccountSet = new HashSet<>();

    @OneToMany(mappedBy = "accountIbanCode", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<TransactionRecord> transactionRecords;

    public void addMultiCurrencyAccount(MultiCurrencyAccount account){
        this.multiCurrencyAccountSet.add(account);
    }
}
