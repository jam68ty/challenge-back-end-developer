package com.example.ebankingbackend.service;

import com.example.ebankingbackend.dto.response.AccountResponse;
import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.MultiCurrencyAccount;
import com.example.ebankingbackend.model.User;
import com.example.ebankingbackend.repository.AccountRepository;
import com.example.ebankingbackend.repository.MultiCurrencyAccountRepository;
import com.example.ebankingbackend.repository.UserRepository;
import com.example.ebankingbackend.util.ObjectMapperUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MultiCurrencyAccountRepository multiCurrencyAccountRepository;

    @Autowired
    private ObjectMapperUtil objectMapperUtil;


    public ResponseEntity<?> createAccount(String currency, double balance, String type) {
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findUserByUsername(authentication.getName()).get();

        String randomIbanCode = generateIbanCode("00762").toString();
        if (accountRepository.findAccountByIbanCode(randomIbanCode).isEmpty()) {
            Account account = new Account();
            account.setIbanCode(randomIbanCode);
            account.setUserId(currentUser);

            accountRepository.save(account);

            var savedAccount = findAccountByIbanCode(randomIbanCode);

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
            logger.info("[AccountService] - Add a new account successfully");
            responseMap.put("code", "success");
            responseMap.put("account", response);
        } else {
            logger.error("[AccountService] - Random iban code is duplicated, retry again");
            responseMap.put("code", "fail");
            responseMap.put("message", "random iban code is duplicated, retry again");
        }
        return ResponseEntity.ok().body(responseMap);
    }

    private Iban generateIbanCode(String bankCode) { //"00762"
        return new Iban.Builder()
                .countryCode(CountryCode.CH)
                .bankCode(bankCode)
                .buildRandom();
    }

    public ResponseEntity<?> createMultiCurrencyAccount(String ibanCode, String currency, double balance, String type) {
        Map<String, Object> responseMap = new HashMap<>();
        var account = findAccountByIbanCode(ibanCode);
        if (ObjectUtils.isNotEmpty(account) && getCurrentUser().equals(account.getUserId())) {
            MultiCurrencyAccount multiCurrencyAccount = new MultiCurrencyAccount();
            if (multiCurrencyAccountRepository.countBySameIbanCodeAndTypeAndCurrency(account, type, currency) > 0) {
                logger.error("[AccountService] - Currency exists!");
                responseMap.put("code", "fail");
                responseMap.put("message", "currency and account type exists in " + ibanCode);
            } else {
                multiCurrencyAccount.setMultiCurrencyAccountId("");
                multiCurrencyAccount.setIbanCode(account);
                multiCurrencyAccount.setCurrency(Monetary.getCurrency(currency).getCurrencyCode());
                multiCurrencyAccount.setType(type);
                multiCurrencyAccount.setBalance(balance);
                multiCurrencyAccountRepository.save(multiCurrencyAccount);
                logger.info("[AccountService] - Add a new multiCurrencyAccount successfully");
                responseMap.put("code", "success");
                responseMap.put("message", "add a new multi currency account to account " + ibanCode);
                responseMap.put("multiCurrencyAccount", multiCurrencyAccount);
            }
        } else {
            logger.error("[AccountService] - This account is not belong to this user");
            responseMap.put("code", "fail");
            responseMap.put("message", "iban code is not exists");
        }

        return ResponseEntity.ok().body(responseMap);
    }


    public ResponseEntity<?> getAccountByIbanCode(String ibanCode) {
        Map<String, Object> responseMap = new HashMap<>();

        var account = findAccountByIbanCode(ibanCode);
        if (ObjectUtils.isNotEmpty(account) && account.getUserId().equals(getCurrentUser())) {
            AccountResponse accountResponse = new AccountResponse();
            objectMapperUtil.objectCovert(account, accountResponse);
            logger.info("[AccountService] - Get account " + account);
            responseMap.put("code", "success");
            responseMap.put("account", accountResponse);
        } else {
            logger.info("[AccountService] - This account is not exists");
            responseMap.put("code", "fail");
            responseMap.put("message", "this account is not exists");
        }

        return ResponseEntity.ok().body(responseMap);
    }

    public ResponseEntity<?> getAccountByUser() {
        Map<String, Object> responseMap = new HashMap<>();
        var userId = getCurrentUser();
        var accounts = accountRepository.findAccountByUserId(userId);
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (var account : accounts) {
            AccountResponse re = new AccountResponse();
            objectMapperUtil.objectCovert(account, re);
            accountResponses.add(re);
        }
        logger.info("[AccountService] - Get accounts by user");
        responseMap.put("code", "success");
        responseMap.put("account", accountResponses);

        return ResponseEntity.ok().body(responseMap);
    }

    public Account findAccountByIbanCode(String ibanCode) {
        return accountRepository.findAccountByIbanCode(ibanCode).orElseThrow(() -> {
            try {
                throw new Exception("Can't find account");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findUserByUsername(authentication.getName()).orElseThrow(() -> {
            try {
                throw new Exception("Can't find user");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
