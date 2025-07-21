package com.example.Banking.system.DTOs;

import com.example.Banking.system.Model.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {

    private String accountNumber;

    private AccountType accountType;

    private BigDecimal balance;

    private List<TransactionDTO> transactions = new ArrayList<>();

}
