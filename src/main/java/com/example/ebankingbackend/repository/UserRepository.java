package com.example.ebankingbackend.repository;

import com.example.ebankingbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE username=:username")
    Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE userId=:userId")
    Optional<User> findUserByUserId(String userId);
}
