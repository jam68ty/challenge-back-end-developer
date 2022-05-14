package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.dto.TransactionRequest;
import com.example.ebankingbackend.dto.TransactionResponse;
import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.TransactionRecord;
import com.example.ebankingbackend.repository.AccountRepository;
import com.example.ebankingbackend.repository.TransactionRepository;
import com.example.ebankingbackend.util.ObjectMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    private ObjectMapperUtil objectMapperUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createTransactionRecord(@RequestBody TransactionRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        var account = accountRepository.findAccountByIbanCode(request.getAccountIBAN());


        TransactionRecord transactionRecord = new TransactionRecord();
        objectMapperUtil.objectCovert(request, transactionRecord);
        transactionRecord.setAccountIbanCode(account.get());
        transactionRecord.setValueDate(LocalDateTime.now());
        transactionRepository.save(transactionRecord);

        TransactionResponse response = new TransactionResponse();
        objectMapperUtil.objectCovert(transactionRecord, response);
        responseMap.put("code", "success");
        responseMap.put("transaction record", response);

        return ResponseEntity.ok().body(responseMap);
    }


}
