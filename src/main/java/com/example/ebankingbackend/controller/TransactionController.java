package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.dto.request.TransactionRequest;
import com.example.ebankingbackend.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/transaction")
@Api(tags = "Transaction 交易紀錄")
public class TransactionController {

    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/createTransactionRecord")
    @ApiOperation(value = "建立交易紀錄", notes = "create transaction record")
    public ResponseEntity<?> createTransactionRecord(@ApiParam(value = "transaction request") @RequestBody TransactionRequest request) {
        return transactionService.createTransactionRecord(request);
    }

    @GetMapping("/getTransactions")
    @ApiOperation(value = "取得交易紀錄", notes = "get transactions")
    public ResponseEntity<?> getTransactions(@ApiParam(name = "month", required = true) @RequestParam(value = "month") Integer month,
                                             @RequestParam(value = "startIndex", required = false, defaultValue = "0") Integer startIndex,
                                             @RequestParam(value = "size", required = false, defaultValue = "0") Integer size) {
        return transactionService.getTransactions(month, startIndex, size);
    }

}
