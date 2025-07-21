package com.example.Banking.system.Controller;

import com.example.Banking.system.DTOs.*;
import com.example.Banking.system.Model.AccountType;
import com.example.Banking.system.Service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class Admin {

    @Autowired
    AccountService accountService;

    @GetMapping("/welcome")
    public String hello(){
        return "welcome admin";
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<?>> withDrawMoney(@Valid @RequestBody WithDrawRequestDTO withDrawRequest){
        return accountService.withDrawMoney(withDrawRequest);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<?>> depositMoney(@Valid @RequestBody DepositRequestDTO depositRequest){
        return accountService.depositMoney(depositRequest);
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<?>> transferMoney(@RequestBody TransferRequestDTO transferRequest){
        return accountService.transferMoney(transferRequest);
    }

    @PostMapping("/create-saving-account")
    public ResponseEntity<ApiResponse<?>> createAccount(@Valid @RequestBody CreateAccountRequest request){
        return accountService.createAccount(request,AccountType.SAVING);
    }

    @PostMapping("/create-current-account")
    public ResponseEntity<ApiResponse<?>> createSavingAccount(@Valid @RequestBody CreateAccountRequest request){
        return accountService.createAccount(request,AccountType.CURRENT);
    }

    @GetMapping("/get-account/{accountNumber}")
    public ResponseEntity<ApiResponse<?>> getAccount(@PathVariable String accountNumber){
        return accountService.getAccount(accountNumber);
    }

    @GetMapping("/get-balance/{accountNumber}")
    public ResponseEntity<ApiResponse<?>> getBalance(@PathVariable String accountNumber){
        return accountService.getBalance(accountNumber);
    }

    @GetMapping("/get-transaction/{accountNumber}")
    public ResponseEntity<ApiResponse<?>> getTransaction(@PathVariable String accountNumber){
        return accountService.getTransaction(accountNumber);
    }


}
