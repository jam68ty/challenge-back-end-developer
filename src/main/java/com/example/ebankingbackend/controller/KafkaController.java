package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.config.KafkaConfig;
import com.example.ebankingbackend.model.Topic;
import com.example.ebankingbackend.model.User;
import com.example.ebankingbackend.repository.TopicRepository;
import com.example.ebankingbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("kafka")
public class KafkaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaController.class);

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping("/publish")
    public String post(@RequestParam("topic_name") final String topicName,
                       @RequestParam(value = "username") final String username) {
        User user = userRepository.findUserByUsername(username);
        if (topicRepository.findTopicByTopicName(topicName) == null) {
            TopicBuilder.name(topicName).build();
        }
        ListenableFuture<SendResult<String, User>> future =
                kafkaTemplate.send(topicName, user);

        future.addCallback(new KafkaSendCallback<String, User>() {

            @Override
            public void onSuccess(SendResult<String, User> result) {
                Topic topic = new Topic();
                topic.setTopicName(topicName);
                topic.setSubscribers(username);
                topicRepository.save(topic);
                LOGGER.info("success send message:{} with offset:{} ", user,
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                LOGGER.error("fail send message! Do somthing....");
            }
        });

        return "Published done";
    }
}
