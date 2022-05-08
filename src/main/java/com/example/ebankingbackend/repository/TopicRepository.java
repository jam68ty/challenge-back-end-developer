package com.example.ebankingbackend.repository;

import com.example.ebankingbackend.model.Topic;
import com.example.ebankingbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic,Long> {
    @Query(value = "SELECT t FROM Topic t WHERE topic_name=:topicName")
    User findTopicByTopicName(String topicName);
}
