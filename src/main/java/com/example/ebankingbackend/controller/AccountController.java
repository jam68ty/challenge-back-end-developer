package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.dto.AccountResponse;
import com.example.ebankingbackend.dto.MultiCurrencyAccountResponse;
import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.MultiCurrencyAccount;
import com.example.ebankingbackend.model.User;
import com.example.ebankingbackend.repository.AccountRepository;
import com.example.ebankingbackend.repository.MultiCurrencyAccountRepository;
import com.example.ebankingbackend.repository.UserRepository;
import com.example.ebankingbackend.util.ObjectMapperUtil;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.money.Monetary;
import java.util.*;

@RestController
@RequestMapping("api/account")
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MultiCurrencyAccountRepository multiCurrencyAccountRepository;

    @Autowired
    private ObjectMapperUtil objectMapperUtil;

    @PostMapping("/createNewAccount")
    public ResponseEntity<?> createNewAccount(@RequestParam(value = "currency") String currency,
                                              @RequestParam(value = "balance", required = false, defaultValue = "0") double balance,
                                              @RequestParam(value = "type", required = false, defaultValue = "saving") String type) {

        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findUserByUsername(authentication.getName()).get();

        String randomIbanCode = generateIbanCode("00762").toString();
        if (accountRepository.findAccountByIbanCode(randomIbanCode).isEmpty()) {
            Account account = new Account();
            account.setIbanCode(randomIbanCode);
            account.setUserId(currentUser);

            accountRepository.save(account);

            var savedAccount =
                    accountRepository.findAccountByIbanCode(randomIbanCode).orElseThrow(() -> {
                        try {
                            throw new Exception("Can't find account");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            MultiCurrencyAccount multiCurrencyAccount = new MultiCurrencyAccount();
            multiCurrencyAccount.setMultiCurrencyAccountId("");
            multiCurrencyAccount.setCurrency(Monetary.getCurrency(currency).getCurrencyCode());
            multiCurrencyAccount.setIbanCode(savedAccount);
            multiCurrencyAccount.setBalance(balance);
            multiCurrencyAccount.setType(type);
            multiCurrencyAccountRepository.save(multiCurrencyAccount);

            savedAccount.addMultiCurrencyAccount(multiCurrencyAccount);
            accountRepository.save(savedAccount);

            AccountResponse response = new AccountResponse();
            objectMapperUtil.objectCovert(savedAccount, response);

            responseMap.put("code", "success");
            responseMap.put("account", response);
        } else {
            responseMap.put("code", "fail");
            responseMap.put("message", "random iban code is duplicated, retry again");
        }
        return ResponseEntity.ok().body(responseMap);
    }

    @PostMapping("/createMultiCurrencyAccount")
    public ResponseEntity<?> createMultiCurrencyAccount(@RequestParam(value = "ibanCode") String ibanCode,
                                                        @RequestParam(value = "currency") String currency,
                                                        @RequestParam(value = "balance", required = false, defaultValue = "0") double balance,
                                                        @RequestParam(value = "type", required = false, defaultValue = "saving") String type) {
        Map<String, Object> responseMap = new HashMap<>();
        var account =
                accountRepository.findAccountByIbanCode(ibanCode).orElseThrow(() -> {
                    try {
                        throw new Exception("Can't find account");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        MultiCurrencyAccount multiCurrencyAccount = new MultiCurrencyAccount();
        if (multiCurrencyAccountRepository.existsByType(type) && multiCurrencyAccountRepository.existsByCurrency(currency)) {
            logger.info("currency exists!");
            responseMap.put("code", "fail");
            responseMap.put("message", "currency and account type exists in " + ibanCode);
        } else {
            multiCurrencyAccount.setMultiCurrencyAccountId("");
            multiCurrencyAccount.setIbanCode(account);
            multiCurrencyAccount.setCurrency(Monetary.getCurrency(currency).getCurrencyCode());
            multiCurrencyAccount.setType(type);
            multiCurrencyAccount.setBalance(balance);
            multiCurrencyAccountRepository.save(multiCurrencyAccount);
            responseMap.put("code", "success");
            responseMap.put("message", "add a new multiCurrencyAccount to account " + ibanCode);
            responseMap.put("multiCurrencyAccount", multiCurrencyAccount);
        }
        return ResponseEntity.ok().body(responseMap);
    }

    @GetMapping("/getAccountByIbanCode")
    public ResponseEntity<?> getAccountByIbanCode(@RequestParam(value = "ibanCode") String ibanCode) {
        Map<String, Object> responseMap = new HashMap<>();
        var account =
                accountRepository.findAccountByIbanCode(ibanCode).orElseThrow(() -> {
                    try {
                        throw new Exception("Can't find account");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        AccountResponse accountResponse = new AccountResponse();
        objectMapperUtil.objectCovert(account, accountResponse);
        responseMap.put("code", "success");
        responseMap.put("account", accountResponse);

        return ResponseEntity.ok().body(responseMap);
    }

    @GetMapping("/getAccountByUser")
    public ResponseEntity<?> getAccountByUser() {
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = userRepository.findUserByUsername(authentication.getName()).orElseThrow(() -> {
            try {
                throw new Exception("Can't find user");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var accounts = accountRepository.findAccountByUserId(userId);
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (var account : accounts) {
            AccountResponse re = new AccountResponse();
            objectMapperUtil.objectCovert(account, re);
            accountResponses.add(re);
        }

        responseMap.put("code", "success");
        responseMap.put("account", accountResponses);

        return ResponseEntity.ok().body(responseMap);
    }

    private Iban generateIbanCode(String bankCode) { //"00762"
        return new Iban.Builder()
                .countryCode(CountryCode.CH)
                .bankCode(bankCode)
                .buildRandom();

    }
}
