package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.dto.CurrencyExchangeResponse;
import com.example.ebankingbackend.dto.MultiCurrencyAccountResponse;
import com.example.ebankingbackend.dto.TransactionRequest;
import com.example.ebankingbackend.dto.TransactionResponse;
import com.example.ebankingbackend.model.MultiCurrencyAccount;
import com.example.ebankingbackend.model.TransactionRecord;
import com.example.ebankingbackend.repository.AccountRepository;
import com.example.ebankingbackend.repository.MultiCurrencyAccountRepository;
import com.example.ebankingbackend.repository.TransactionRepository;
import com.example.ebankingbackend.repository.UserRepository;
import com.example.ebankingbackend.util.ObjectMapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@RestController
@RequestMapping("api/transaction")
public class TransactionController {

    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MultiCurrencyAccountRepository multiCurrencyAccountRepository;

    @Autowired
    private ObjectMapperUtil objectMapperUtil;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/create")
    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseEntity<?> createTransactionRecord(@RequestBody TransactionRequest request) {
        Map<String, Object> responseMap = new HashMap<>();

        var subAccount = multiCurrencyAccountRepository.findMultiCurrencyAccountById(request.getMultiCurrencyAccountId()).orElseThrow(()->
        {
            try {
                throw new Exception("Can't find this multi currency account");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setAmount(request.getAmount());
        transactionRecord.setMultiCurrencyAccountId(subAccount);
        transactionRecord.setIbanCode(subAccount.getIbanCode());
        transactionRecord.setCurrency(request.getTransactionCurrency());
        transactionRecord.setValueDate(LocalDateTime.now());
        transactionRecord.setDescription(request.getDescription());
        transactionRecord.setType(request.getType());
        transactionRepository.save(transactionRecord);

        TransactionResponse response = new TransactionResponse();
        objectMapperUtil.objectCovert(transactionRecord, response);

        //sava transaction
        kafkaTemplate.send("tx", transactionRecord.getTransactionId().toString(), transactionRecord);
        //credit or debit

        if (StringUtils.equals("debit", request.getType())) {
            if (StringUtils.equals(request.getTransactionCurrency(), subAccount.getCurrency())) {
                subAccount.setBalance(subAccount.getBalance() + request.getAmount());
            } else {
                var covertAmount = currencyExchange(subAccount.getCurrency(), request.getTransactionCurrency(), request.getAmount());
                subAccount.setBalance(subAccount.getBalance() + covertAmount.getResult());
            }
        } else if (StringUtils.equals("credit", request.getType())) {
            if (StringUtils.equals(request.getTransactionCurrency(), subAccount.getCurrency())) {
                if (subAccount.getBalance() >= request.getAmount()) {
                    subAccount.setBalance(subAccount.getBalance() - request.getAmount());
                } else {
                    responseMap.put("code", "fail");
                    responseMap.put("message", "Insufficient balance");
                    return ResponseEntity.badRequest().body(responseMap);
                }
            } else {
                var covertAmount = currencyExchange(subAccount.getCurrency(), request.getTransactionCurrency(), request.getAmount());
                if (subAccount.getBalance() >= covertAmount.getResult()) {
                    subAccount.setBalance(subAccount.getBalance() - covertAmount.getResult());
                } else {
                    responseMap.put("code", "fail");
                    responseMap.put("message", "Insufficient balance");
                    return ResponseEntity.badRequest().body(responseMap);
                }

            }
        } else {
            responseMap.put("code", "fail");
            responseMap.put("message", "type error");
            return ResponseEntity.badRequest().body(responseMap);
        }
        MultiCurrencyAccountResponse multiCurrencyAccountResponse = new MultiCurrencyAccountResponse();
        objectMapperUtil.objectCovert(subAccount, multiCurrencyAccountResponse);
        multiCurrencyAccountResponse.setMultiCurrencyAccountId(subAccount.getMultiCurrencyAccountId());
        response.setMultiCurrencyAccountId(multiCurrencyAccountResponse);
        multiCurrencyAccountRepository.save(subAccount);
        kafkaTemplate.send("account", transactionRecord.getTransactionId().toString(), subAccount);

        responseMap.put("code", "success");
        responseMap.put("transaction record", response);
        return ResponseEntity.ok().body(responseMap);
    }

    @GetMapping("/getTransactions")
    public ResponseEntity<?> getTransactions(@RequestParam(value = "month") Integer month,
                                             @RequestParam(value = "startIndex") Integer startIndex,
                                             @RequestParam(value = "size") Integer size) {
        Map<String, Object> responseMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        var userId = userRepository.findUserByUsername(username).get();
        var sortMethod = Sort.by(Sort.Direction.DESC, "valueDate");
        var pageRequest = (startIndex == 0 && size == 0) ? Pageable.unpaged()
                : PageRequest.of(startIndex, size, sortMethod);
        var trans = transactionRepository.findByMonth(month, userId, pageRequest).get();

        responseMap.put("totalPages", trans.getTotalPages());
        responseMap.put("totalElements", trans.getTotalElements());
        var returnLatestTransaction = new LinkedList<TransactionResponse>();
        for (var latestRes : trans.getContent()) {
            TransactionResponse response = new TransactionResponse();
            objectMapperUtil.objectCovert(latestRes, response);
            returnLatestTransaction.add(response);
        }
        responseMap.put("code", "success");
        responseMap.put("transactions", returnLatestTransaction);
        return ResponseEntity.ok().body(responseMap);
    }

    private CurrencyExchangeResponse currencyExchange(String to, String from, double amount) {
        String url = "https://api.apilayer.com/exchangerates_data/convert?to=" + to + "&from=" + from + "&amount=" + Double.toString(amount);

        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", "ZDZ3WZYo3GaJgGJrv58JEyqj3By9tJCN");

        HttpEntity requestEntity = new HttpEntity(headers);

        ResponseEntity<CurrencyExchangeResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, CurrencyExchangeResponse.class);
        return response.getBody();
    }
}
