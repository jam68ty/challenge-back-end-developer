package com.example.ebankingbackend.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String currency;
    private double amount;
    private String accountIBAN;
    private String description;
}
