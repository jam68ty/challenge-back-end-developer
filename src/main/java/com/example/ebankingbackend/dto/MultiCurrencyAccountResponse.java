package com.example.ebankingbackend.dto;

import lombok.Data;

@Data
public class MultiCurrencyAccountResponse {

    private String currency;
    private String type;
    private double balance;
    private String multiCurrencyAccountId;
}
