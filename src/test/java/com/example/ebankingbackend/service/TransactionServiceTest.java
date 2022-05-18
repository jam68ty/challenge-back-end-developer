package com.example.ebankingbackend.service;

import com.example.ebankingbackend.dto.request.TransactionRequest;
import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.TransactionRecord;
import com.example.ebankingbackend.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@SpringBootTest(properties = "spring.profiles.active=test")
class TransactionServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() throws JSONException {
        String username = "user1";
        String password = "12345";
        String email = "user1@mail.com";
        authenticationService.saveUser(username, password, email);
        createParentAccount();
    }

    @Test
    @WithMockUser(username = "user1")
    void createTransactionRecord() throws JSONException, JsonProcessingException {

        String multiCurrencyAccountId = "MCA000000000001";
        double amount = 10;
        String type = "debit";
        String transactionCurrency = "USD";
        TransactionRequest request = new TransactionRequest();
        request.setMultiCurrencyAccountId(multiCurrencyAccountId);
        request.setAmount(amount);
        request.setType(type);
        request.setTransactionCurrency(transactionCurrency);
        request.setDescription("test");

        var response = transactionService.createTransactionRecord(request);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = objectMapper.writeValueAsString(response.getBody());
        JSONObject result = new JSONObject(jsonString);
        var record = result.getJSONObject("transaction record");
        var account = record.getJSONObject("multiCurrencyAccountId");

        Assertions.assertThat(Double.parseDouble(record.get("amount").toString())).isEqualTo(amount);
        Assertions.assertThat(record.get("transactionId").toString()).isNotEmpty();
        Assertions.assertThat(record.get("currency").toString()).isEqualTo(transactionCurrency);
        Assertions.assertThat(record.get("type").toString()).isEqualTo(type);
        Assertions.assertThat(account.get("multiCurrencyAccountId").toString()).isEqualTo(multiCurrencyAccountId);
    }

    @Test
    @WithMockUser(username = "user1")
    void getTransactions() throws JSONException, JsonProcessingException {
        var response = transactionService.getTransactions(5, 0 ,10);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = objectMapper.writeValueAsString(response.getBody());
        JSONObject result = new JSONObject(jsonString);

        System.out.println(result);
        Assertions.assertThat(result.get("code").toString()).isEqualTo("success");
    }

    private String createParentAccount() throws JSONException {
        String currency = "USD";
        double balance = 0;
        String type = "saving";
        var response = accountService.createAccount(currency, balance, type);
        String jsonInString = new Gson().toJson(response.getBody());
        JSONObject result = new JSONObject(jsonInString);
        var account = new JSONObject(result.get("account").toString());
        return account.get("ibanCode").toString();
    }

}