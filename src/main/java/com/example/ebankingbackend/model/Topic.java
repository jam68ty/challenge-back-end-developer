package com.example.ebankingbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "topic")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;
    @Column(name = "topic_name")
    private String topicName;
    @Column(name = "subscribers")
    private String subscribers;
}
