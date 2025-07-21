package com.example.Banking.system.Service;

import com.example.Banking.system.DTOs.*;
import com.example.Banking.system.Model.*;
import com.example.Banking.system.Repository.AccountRepo;
import com.example.Banking.system.Repository.CustomUserRepo;
import com.example.Banking.system.Repository.RoleRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class MyUserService {

    @Autowired
    CustomUserRepo customUserRepo;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    AccountRepo accountRepo;

    public ResponseEntity<?> createUser(@Valid SignUpRequest request) {


        if (customUserRepo.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>(Map.of("message","Email already exists"), HttpStatus.BAD_REQUEST);
        }else if (customUserRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            return new ResponseEntity<>(Map.of("message","Phone Number already exists"),HttpStatus.BAD_REQUEST);
        } else if (customUserRepo.existsByUserName(request.getUserName())) {
            return new ResponseEntity<>(Map.of("message","User Name already exists"),HttpStatus.BAD_REQUEST);
        }

        Set<Role> roles = request.getRoles().stream().map(
                role -> roleRepo.findByRoleName(role)
                        .orElseThrow(()->new RuntimeException("Role Not Found " + role))
        ).collect(Collectors.toSet());

        CustomUser customUser = CustomUser.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .city(request.getCity())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        customUserRepo.save(customUser);

        return new ResponseEntity<>(Map.of("Message :","User created SuccessFully.."), HttpStatus.CREATED);
    }

    public ResponseEntity<?> verify(@Valid SignInRequest request) {
        try{

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(),request.getPassword())
            );

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.getToken(request.getUserName(),customUserDetails.getCustomUser().getRoles());

            return new ResponseEntity<>(Map.of("token :",token),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("Message :",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ApiResponse<?>> updateUser(@Valid SignUpRequest request, String name) {

        CustomUser customUser = customUserRepo.findByUserName(name);

        if (customUser == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "User not found", null), HttpStatus.NOT_FOUND);
        }

        CustomUser emailOwner = customUserRepo.findByEmail(request.getEmail());
        if (emailOwner != null && !emailOwner.getId().equals(customUser.getId())) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Email already exists", null), HttpStatus.BAD_REQUEST);
        }

        CustomUser phoneOwner = customUserRepo.findByPhoneNumber(request.getPhoneNumber());
        if (phoneOwner != null && !phoneOwner.getId().equals(customUser.getId())) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Phone Number already exists", null), HttpStatus.BAD_REQUEST);
        }

        CustomUser nameOwner = customUserRepo.findByUserName(request.getUserName());
        if (nameOwner != null && !nameOwner.getId().equals(customUser.getId())) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Name already exists", null), HttpStatus.BAD_REQUEST);
        }


        Set<Role> roles = request.getRoles().stream().map(
                role -> roleRepo.findByRoleName(role)
                        .orElseThrow(()->new RuntimeException("Role Not Found "+role))
        ).collect(Collectors.toSet());

        customUser.setUserName(request.getUserName());
        customUser.setEmail(request.getEmail());
        customUser.setCity(request.getCity());
        customUser.setPassword(passwordEncoder.encode(request.getPassword()));
        customUser.setPhoneNumber(request.getPhoneNumber());
        customUser.setRoles(roles);

        try {
            customUserRepo.save(customUser);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Failed to update user: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<>(new ApiResponse<>(true,"Updated SuccessFully",customUser),HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<?>> getUserDetails(String userName) {

        CustomUser customUser = customUserRepo.findByUserName(userName);

        return new ResponseEntity<>(new ApiResponse<>(true,"Fetched",customUser),HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<?>> getAccountDetails(String userName, String accountType){

        if(!customUserRepo.existsByUserName(userName)){
            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid user", null), HttpStatus.NOT_FOUND);
        }

        CustomUser user = customUserRepo.findByUserName(userName);

        Account account = new Account();

        try{
            account = accountRepo.findByUser_IdAndAccountType(user.getId(),AccountType.valueOf(accountType));
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid AccountType", null), HttpStatus.NOT_FOUND);
        }

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false, "User Not Have " + accountType + " account ", null), HttpStatus.NOT_FOUND);
        }

        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .transactions(null)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(true,"Success",accountResponseDto),HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<?>> getTransaction(String userName,String accountType){

        if(!customUserRepo.existsByUserName(userName)){
            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid user", null), HttpStatus.NOT_FOUND);
        }

        CustomUser user = customUserRepo.findByUserName(userName);

        Account account = accountRepo.findByUser_IdAndAccountType(user.getId(),AccountType.valueOf(accountType));

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false, "User Not Have " + accountType + " account ", null), HttpStatus.NOT_FOUND);
        }

        List<TransactionDTO> transactionDTOList = new ArrayList<>();

        for(Transaction transaction : account.getTransactions()){
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .transactionID(transaction.getTransactionId())
                    .createdAt(transaction.getCreatedAt())
                    .accountNumber(transaction.getAccount().getAccountNumber())
                    .toAccountNum(transaction.getToAccountNum())
                    .fromAccountNum(transaction.getFromAccountNum())
                    .transactionType(transaction.getTransactionType())
                    .amount(transaction.getAmount())
                    .build();

            transactionDTOList.add(transactionDTO);
        }

        return new ResponseEntity<>(new ApiResponse<>(true,"Fetched transactions",transactionDTOList),HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<?>> getBalance(String userName,String accountType) {

        if(!customUserRepo.existsByUserName(userName)){
            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid user", null), HttpStatus.NOT_FOUND);
        }

        CustomUser user = customUserRepo.findByUserName(userName);

        Account account = accountRepo.findByUser_IdAndAccountType(user.getId(),AccountType.valueOf(accountType));

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false, "User Not Have " + accountType + " account ", null), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new ApiResponse<>(true,"fetched Balance SuccessFully.",account.getBalance()),HttpStatus.OK);
    }
}
