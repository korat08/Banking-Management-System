package com.example.Banking.system.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequestDTO {

    @NotNull(message = "Sender account number must not be null")
    private String senderAccountNumber;

    @NotNull(message = "Receiver account number must not be null")
    private String receiverAccountNumber;

    @NotNull
    @DecimalMin(value = "1", message = "Transfer amount must be greater than zero")
    private BigDecimal amount;
}
