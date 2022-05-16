package com.example.ebankingbackend.dto.response;

import com.example.ebankingbackend.dto.response.MultiCurrencyAccountResponse;
import com.example.ebankingbackend.model.Account;
import lombok.Data;

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
