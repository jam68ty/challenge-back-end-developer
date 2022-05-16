package com.example.ebankingbackend.config;

import com.example.ebankingbackend.model.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "tx")
    public void consumeTransaction(TransactionRecord transactionRecord) throws InterruptedException {
        LOGGER.info("Consumed JSON Transaction: {} ", transactionRecord);
    }
}
