package com.example.ebankingbackend.repository;

import com.example.ebankingbackend.model.Account;
import com.example.ebankingbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query(value = "SELECT a FROM Account a WHERE a.ibanCode = :ibanCode")
    Optional<Account> findAccountByIbanCode(String ibanCode);

    @Query(value = "SELECT a FROM Account a WHERE a.userId = :userId")
    Set<Account> findAccountByUserId(User userId);
}
