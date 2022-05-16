package com.example.ebankingbackend.dto;

import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.MultiCurrencyAccount;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private UUID transactionId;
    private double amount;
    private String currency;
    private LocalDateTime valueDate;
    private String description;
    private String type;
    private Account ibanCode;
    private MultiCurrencyAccountResponse multiCurrencyAccountId;
}
