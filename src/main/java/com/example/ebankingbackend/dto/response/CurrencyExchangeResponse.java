package com.example.ebankingbackend.dto.response;

import lombok.Data;

@Data
public class CurrencyExchangeResponse {

    private boolean success;
    private String date;
    private double result;
    private ExchangeInfoResponse info;
}
