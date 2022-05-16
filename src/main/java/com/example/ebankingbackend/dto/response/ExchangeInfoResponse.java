package com.example.ebankingbackend.dto.response;

import lombok.Data;

@Data
public class ExchangeInfoResponse {
    private long timestamp;
    private double rate;
}
