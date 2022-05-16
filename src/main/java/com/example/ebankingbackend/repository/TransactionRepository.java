package com.example.ebankingbackend.repository;

import com.example.ebankingbackend.model.TransactionRecord;
import com.example.ebankingbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, String> {

    @Query("SELECT t FROM TransactionRecord t, Account a WHERE MONTH(value_date)= :month " +
            "AND a.userId = :userId and t.ibanCode=a.ibanCode")
    Optional<Page<TransactionRecord>> findByMonth(Integer month, User userId, Pageable page);
}
