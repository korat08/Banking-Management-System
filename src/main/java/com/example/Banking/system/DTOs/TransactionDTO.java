package com.example.Banking.system.DTOs;

import com.example.Banking.system.Model.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private int transactionID;
    private String accountNumber;
    private TransactionType transactionType;
    private String fromAccountNum;
    private String toAccountNum;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
