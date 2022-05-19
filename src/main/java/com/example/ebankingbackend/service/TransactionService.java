package com.example.ebankingbackend.service;

import com.example.ebankingbackend.dto.request.TransactionRequest;
import com.example.ebankingbackend.dto.response.CurrencyExchangeResponse;
import com.example.ebankingbackend.dto.response.MultiCurrencyAccountResponse;
import com.example.ebankingbackend.dto.response.TransactionResponse;
import com.example.ebankingbackend.model.MultiCurrencyAccount;
import com.example.ebankingbackend.model.TransactionRecord;
import com.example.ebankingbackend.model.User;
import com.example.ebankingbackend.repository.AccountRepository;
import com.example.ebankingbackend.repository.MultiCurrencyAccountRepository;
import com.example.ebankingbackend.repository.TransactionRepository;
import com.example.ebankingbackend.repository.UserRepository;
import com.example.ebankingbackend.util.ObjectMapperUtil;
import org.apache.commons.lang3.ObjectUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {

    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

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

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseEntity<?> createTransactionRecord(TransactionRequest request) {
        Map<String, Object> responseMap = new HashMap<>();

        var currentUser = getCurrentUser();
        var subAccount = getMultiCurrencyAccount(request.getMultiCurrencyAccountId());

        if (ObjectUtils.isNotEmpty(subAccount) && currentUser.equals(subAccount.getIbanCode().getUserId())) {
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setTransactionId(UUID.randomUUID());
            transactionRecord.setAmount(request.getAmount());
            transactionRecord.setMultiCurrencyAccountId(subAccount);
            transactionRecord.setIbanCode(subAccount.getIbanCode());
            transactionRecord.setCurrency(request.getTransactionCurrency());
            transactionRecord.setValueDate(LocalDateTime.now());
            transactionRecord.setType(request.getType());

            TransactionResponse response = new TransactionResponse();
            objectMapperUtil.objectCovert(transactionRecord, response);
            //credit or debit
            if (StringUtils.equals("debit", request.getType())) {
                transactionRecord.setDescription("success");
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
                        transactionRecord.setDescription("success");
                    } else {
                        transactionRecord.setDescription("fail");
                        transactionRepository.save(transactionRecord);
                        responseMap.put("code", "fail");
                        responseMap.put("message", "Insufficient balance");
                        logger.error("[TransactionService] - Insufficient balance");
                        return ResponseEntity.badRequest().body(responseMap);
                    }
                } else {
                    var covertAmount = currencyExchange(subAccount.getCurrency(), request.getTransactionCurrency(), request.getAmount());
                    if (subAccount.getBalance() >= covertAmount.getResult()) {
                        subAccount.setBalance(subAccount.getBalance() - covertAmount.getResult());
                        transactionRecord.setDescription("success");
                    } else {
                        transactionRecord.setDescription("fail");
                        transactionRepository.save(transactionRecord);
                        responseMap.put("code", "fail");
                        responseMap.put("message", "Insufficient balance");
                        logger.error("[TransactionService] - Insufficient balance");
                        return ResponseEntity.badRequest().body(responseMap);
                    }

                }
            } else {
                responseMap.put("code", "fail");
                responseMap.put("message", "type error");
                logger.error("[TransactionService] - Transaction type error");
                return ResponseEntity.badRequest().body(responseMap);
            }
            transactionRepository.save(transactionRecord);
            kafkaTemplate.send("tx", transactionRecord.getTransactionId().toString(), transactionRecord);
            MultiCurrencyAccountResponse multiCurrencyAccountResponse = new MultiCurrencyAccountResponse();
            objectMapperUtil.objectCovert(subAccount, multiCurrencyAccountResponse);
            multiCurrencyAccountResponse.setMultiCurrencyAccountId(subAccount.getMultiCurrencyAccountId());
            response.setMultiCurrencyAccountId(multiCurrencyAccountResponse);
            response.setDescription(transactionRecord.getDescription());
            multiCurrencyAccountRepository.save(subAccount);
            kafkaTemplate.send("account", transactionRecord.getTransactionId().toString(), subAccount);

            responseMap.put("code", "success");
            responseMap.put("transaction record", response);
            logger.info("[TransactionService] - Add transaction successfully");

        } else {
            responseMap.put("code", "fail");
            responseMap.put("message", "the account iban code is error");
            logger.error("[TransactionService] - This iban code is not exists in this user");
        }
        return ResponseEntity.ok().body(responseMap);

    }

    public ResponseEntity<?> getTransactions(Integer month, Integer startIndex, Integer size) {
        Map<String, Object> responseMap = new HashMap<>();
        var userId = getCurrentUser();
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
            MultiCurrencyAccountResponse multiCurrencyAccountResponse = new MultiCurrencyAccountResponse();
            objectMapperUtil.objectCovert(latestRes.getMultiCurrencyAccountId(), multiCurrencyAccountResponse);
            response.setMultiCurrencyAccountId(multiCurrencyAccountResponse);
            returnLatestTransaction.add(response);
        }
        responseMap.put("code", "success");
        responseMap.put("transactions", returnLatestTransaction);
        logger.info("[TransactionService] - Get Transactions " + trans.getContent());
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

    private MultiCurrencyAccount getMultiCurrencyAccount(String multiCurrencyAccountId) {
        return multiCurrencyAccountRepository.findMultiCurrencyAccountById(multiCurrencyAccountId).orElseThrow(() ->
        {
            try {
                throw new Exception("Can't find this multi currency account");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
