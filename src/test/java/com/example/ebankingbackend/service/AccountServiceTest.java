package com.example.ebankingbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        String username = "user";
        String password = "12345";
        String email = "user@mail.com";
        authenticationService.saveUser(username, password, email);
    }

    @Test
    @WithMockUser(username = "user")
    void createAccount() throws JSONException {
        String currency = "USD";
        double balance = 0;
        String type = "saving";

        var response = accountService.createAccount(currency, balance, type);
        String jsonInString = new Gson().toJson(response.getBody());
        JSONObject result = new JSONObject(jsonInString);
        var account = new JSONObject(result.get("account").toString());
        var subAccount = new JSONObject(account.getJSONArray("multiCurrencyAccountSet").get(0).toString());

        Assertions.assertThat(account.get("ibanCode").toString()).isNotEmpty();
        Assertions.assertThat(subAccount.get("currency").toString()).isEqualTo(currency);
        Assertions.assertThat(subAccount.get("type").toString()).isEqualTo(type);
        Assertions.assertThat(Double.parseDouble(subAccount.get("balance").toString())).isEqualTo(balance);

    }

    @Test
    @WithMockUser(username = "user")
    void createMultiCurrencyAccount_shouldSuccess() throws JSONException, JsonProcessingException {
        String ibanCode = createParentAccount();

        String currency = "USD";
        double balance = 0;
        String type = "checking";
        var response = accountService.createMultiCurrencyAccount(ibanCode, currency, balance, type);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(response.getBody());
        JSONObject result = new JSONObject(jsonString);

        var subAccount = new JSONObject(result.getJSONObject("multiCurrencyAccount").toString());

        Assertions.assertThat(subAccount.get("currency").toString()).isEqualTo(currency);
        Assertions.assertThat(subAccount.get("type").toString()).isEqualTo(type);
        Assertions.assertThat(Double.parseDouble(subAccount.get("balance").toString())).isEqualTo(balance);

    }

    @Test
    @WithMockUser(username = "user")
    void createMultiCurrencyAccount_shouldFail() throws JSONException, JsonProcessingException {
        String ibanCode = createParentAccount();
        String currency = "USD";
        double balance = 0;
        String type = "saving";
        var response = accountService.createMultiCurrencyAccount(ibanCode, currency, balance, type);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(response.getBody());
        JSONObject result = new JSONObject(jsonString);

        Assertions.assertThat(result.get("code").toString()).isEqualTo("fail");
    }

    @Test
    @WithMockUser(username = "user")
    void getAccountByIbanCode() throws JSONException, JsonProcessingException {

        String ibanCode = createParentAccount();

        var response = accountService.getAccountByIbanCode(ibanCode);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(response.getBody());
        JSONObject result = new JSONObject(jsonString);
        var account = result.getJSONObject("account");

        Assertions.assertThat(account.get("ibanCode").toString()).isEqualTo(ibanCode);

    }

    @Test
    @WithMockUser(username = "user")
    void getAccountByUser() throws JsonProcessingException, JSONException {
        var response = accountService.getAccountByUser();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(response.getBody());
        JSONObject result = new JSONObject(jsonString);

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