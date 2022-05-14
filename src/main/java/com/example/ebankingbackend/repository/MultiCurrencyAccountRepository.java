package com.example.ebankingbackend.repository;

import com.example.ebankingbackend.model.MultiCurrencyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultiCurrencyAccountRepository extends JpaRepository<MultiCurrencyAccount, Long> {

    boolean existsByCurrency(String currency);
    boolean existsByType(String type);
}
