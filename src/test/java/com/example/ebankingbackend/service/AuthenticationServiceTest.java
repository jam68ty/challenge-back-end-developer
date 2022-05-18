package com.example.ebankingbackend.service;

import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(properties = "spring.profiles.active=test")
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void testSaveUser() throws JSONException {

        String username = "test";
        String password = "12345";
        String email = "test@mail.com";

        var response = authenticationService.saveUser(username, password, email);
        String jsonInString = new Gson().toJson(response.getBody());
        JSONObject result = new JSONObject(jsonInString);

        Assertions.assertThat(result.get("token").toString()).isNotEmpty();
        Assertions.assertThat(result.get("username").toString()).isEqualTo(username);
    }

    @Test
    public void testLoginUser() throws JSONException {
        String expectedMessage = "Logged In";
        String username = "test";
        String password = "12345";
        var response = authenticationService.loginUser(username, password);
        String jsonInString = new Gson().toJson(response.getBody());
        JSONObject result = new JSONObject(jsonInString);

        Assertions.assertThat(result.get("token").toString()).isNotEmpty();
        Assertions.assertThat(result.get("message").toString()).isEqualTo(expectedMessage);
    }

    @Test
    @WithMockUser(username = "test")
    public void testGetUsername() {
        String expectedUsername = "test";
        var result = authenticationService.getUserName();
        Assertions.assertThat(result.get("username").toString()).isEqualTo(expectedUsername);

    }
}
