package com.example.Banking.system.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithDrawRequestDTO {

    @NotNull
    private String accountNumber;

    @NotNull
    @DecimalMin(value = "1", message = "Withdrawal amount must be greater than zero")
    private BigDecimal amount;
}
