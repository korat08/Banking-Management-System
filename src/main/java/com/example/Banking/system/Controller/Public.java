package com.example.Banking.system.Controller;

import com.example.Banking.system.DTOs.SignInRequest;
import com.example.Banking.system.DTOs.SignUpRequest;
import com.example.Banking.system.Service.MyUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bank")
public class Public {

    @Autowired
    MyUserService myUserService;

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest signupRequest){
        return myUserService.createUser(signupRequest);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> SignIN(@Valid @RequestBody SignInRequest request){
        return myUserService.verify(request);
    }
}
