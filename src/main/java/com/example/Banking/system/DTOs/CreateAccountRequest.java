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
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest{

    @NotNull
    private String userName;

    @NotNull
    @DecimalMin(value = "1000.00",message = "Minimum initial balance should be 1000.00 rupes")
    private BigDecimal initialBalance;

}
