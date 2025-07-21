package com.example.Banking.system.Controller;

import com.example.Banking.system.DTOs.ApiResponse;
import com.example.Banking.system.DTOs.SignUpRequest;
import com.example.Banking.system.Service.AccountService;
import com.example.Banking.system.Service.MyUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class User {

    @Autowired
    MyUserService myUserService;

    @Autowired
    AccountService accountService;

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome user";
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<?>> updateUser(@Valid @RequestBody SignUpRequest request, Authentication authentication){
        return myUserService.updateUser(request,authentication.getName());
    }

    @GetMapping("/get-profile")
    public ResponseEntity<ApiResponse<?>> getUserDetails(Authentication authentication){
        return myUserService.getUserDetails(authentication.getName());
    }

    @GetMapping("/get-account/{accountType}")
    public ResponseEntity<ApiResponse<?>> getAccount(Authentication authentication,@PathVariable String accountType){
        return myUserService.getAccountDetails(authentication.getName(),accountType);
    }

    @GetMapping("/get-balance/{accountType}")
    public ResponseEntity<ApiResponse<?>> getBalance(Authentication authentication,@PathVariable String accountType){
        return myUserService.getBalance(authentication.getName(),accountType);
    }

    @GetMapping("/get-transaction/{accountType}")
    public ResponseEntity<ApiResponse<?>> getTransaction(Authentication authentication,@PathVariable String accountType){
        return myUserService.getTransaction(authentication.getName(),accountType);
    }



}
