package com.example.ebankingbackend.dto;

import lombok.Data;

@Data
public class CurrencyExchangeResponse {

    private boolean success;
    private String date;
    private double result;
    private ExchangeInfo info;
}
