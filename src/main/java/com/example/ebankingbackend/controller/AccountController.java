package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.dto.response.AccountResponse;
import com.example.ebankingbackend.service.AccountService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/account")
@Api(tags = "Account 帳戶")
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "建立帳戶", notes = "create account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = AccountResponse.class)})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/createNewAccount")
    public ResponseEntity<?> createNewAccount(@ApiParam(value = "currency", required = true) @RequestParam(value = "currency") String currency,
                                              @ApiParam(value = "balance", defaultValue = "0") @RequestParam(value = "balance", required = false, defaultValue = "0") double balance,
                                              @ApiParam(value = "type (saving, checking)", defaultValue = "saving") @RequestParam(value = "type", required = false, defaultValue = "saving") String type) {

        return accountService.createAccount(currency, balance, type);

    }

    @ApiOperation(value = "建立多貨幣帳戶(在指定的帳戶下)", notes = "create multi currency account under specific account")
    @PostMapping("/createMultiCurrencyAccount")
    public ResponseEntity<?> createMultiCurrencyAccount(@ApiParam(value = "iban code", required = true) @RequestParam(value = "ibanCode") String ibanCode,
                                                        @ApiParam(value = "currency", required = true) @RequestParam(value = "currency") String currency,
                                                        @ApiParam(value = "balance", defaultValue = "0") @RequestParam(value = "balance", required = false, defaultValue = "0") double balance,
                                                        @ApiParam(value = "type (saving, checking)", defaultValue = "saving") @RequestParam(value = "type", required = false, defaultValue = "saving") String type) {

        return accountService.createMultiCurrencyAccount(ibanCode, currency, balance, type);
    }

    @ApiOperation(value = "取得指定帳戶", notes = "get account by iban code")
    @GetMapping("/getAccountByIbanCode")
    public ResponseEntity<?> getAccountByIbanCode(@ApiParam(value = "iban code", required = true) @RequestParam(value = "ibanCode") String ibanCode) {
        return accountService.getAccountByIbanCode(ibanCode);
    }

    @ApiOperation(value = "取得使用者的所有帳戶", notes = "get account by user")
    @GetMapping("/getAccountByUser")
    public ResponseEntity<?> getAccountByUser() {
        return accountService.getAccountByUser();
    }


}
