package com.example.ebankingbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

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
@Proxy(lazy = false)
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "iban_code", nullable = false)
    private String ibanCode;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id", updatable = false)
    private User userId;

    @OneToMany(mappedBy = "ibanCode", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<MultiCurrencyAccount> multiCurrencyAccountSet = new HashSet<>();


    public void addMultiCurrencyAccount(MultiCurrencyAccount account) {
        this.multiCurrencyAccountSet.add(account);
    }
}
