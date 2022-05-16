package com.example.ebankingbackend.dto.request;

import lombok.Data;

@Data
public class TransactionRequest {

    private String multiCurrencyAccountId;
    private String transactionCurrency;
    private double amount;
    private String description;
    private String type;

}
