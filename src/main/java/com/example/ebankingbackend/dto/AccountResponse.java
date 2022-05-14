package com.example.ebankingbackend.dto;

import com.example.ebankingbackend.model.MultiCurrencyAccount;
import com.example.ebankingbackend.model.User;
import lombok.Data;

import java.util.Set;

@Data
public class AccountResponse {
    private String ibanCode;
    private User userId;
    private Set<MultiCurrencyAccountResponse> multiCurrencyAccountSet;
}
