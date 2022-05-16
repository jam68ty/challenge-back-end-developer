package com.example.ebankingbackend.repository;

import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.MultiCurrencyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MultiCurrencyAccountRepository extends JpaRepository<MultiCurrencyAccount, Long> {

    boolean existsByCurrency(String currency);
    boolean existsByType(String type);

    @Query(value = "SELECT m FROM MultiCurrencyAccount m WHERE m.ibanCode = :ibanCode")
    List<MultiCurrencyAccount> findMultiCurrencyAccountByIbanCode(Account ibanCode);


    @Query(value = "SELECT m FROM MultiCurrencyAccount m WHERE m.multiCurrencyAccountId = :id")
    Optional<MultiCurrencyAccount> findMultiCurrencyAccountById(String id);

    @Query(value = "SELECT m FROM MultiCurrencyAccount m WHERE m.ibanCode = :ibanCode AND m.currency = :currency")
    List<MultiCurrencyAccount> findMultiCurrencyAccountByIbanCodeAndCurrency(Account ibanCode, String currency);
}
