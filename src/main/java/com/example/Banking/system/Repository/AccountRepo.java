package com.example.Banking.system.Repository;

import com.example.Banking.system.Model.Account;
import com.example.Banking.system.Model.AccountType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account,Integer> {


    @Query(value = "select count(*) from Account")
    int countAll();

    boolean existsByUserIdAndAccountType(Long id, AccountType accountType);

    Account findByAccountNumber(@NotNull String accountNumber);

    Account findByUser_Id(Long userId);

    Account findByUser_IdAndAccountType(Long id, AccountType accountType);
}
