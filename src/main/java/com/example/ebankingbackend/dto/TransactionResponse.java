package com.example.ebankingbackend.dto;

import com.example.ebankingbackend.model.Account;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String transactionId;
    private String currency;
    private double amount;
    private Account accountIbanCode;
    private String description;
    private LocalDateTime valueDate;
}
