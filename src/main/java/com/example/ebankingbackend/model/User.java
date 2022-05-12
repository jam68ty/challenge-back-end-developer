package com.example.ebankingbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_serial")
    private int userSerial;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;

    public User(String userId, String username, String password, String email) {
        this.userId = "p-" + String.format("%012d", userSerial);;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @PostPersist
    public void createUserId() {
        this.userId = "p-" + String.format("%012d", userSerial);
    }

}
