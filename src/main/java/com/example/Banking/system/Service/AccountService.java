package com.example.Banking.system.Service;

import com.example.Banking.system.DTOs.*;
import com.example.Banking.system.Model.*;
import com.example.Banking.system.Repository.AccountRepo;
import com.example.Banking.system.Repository.CustomUserRepo;
import com.example.Banking.system.Repository.TransactionRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    CustomUserRepo customUserRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @Transactional
    public ResponseEntity<ApiResponse<?>> createAccount(@Valid CreateAccountRequest request,AccountType accountType) {

        if(!customUserRepo.existsByUserName(request.getUserName())){
            return new ResponseEntity<>(new ApiResponse<>(false,"User Not Found",null), HttpStatus.BAD_REQUEST);
        }

        CustomUser user = customUserRepo.findByUserName(request.getUserName());

        if(accountRepo.existsByUserIdAndAccountType(user.getId(),accountType)){
            return new ResponseEntity<>(new ApiResponse<>(false,"User Already have account",null), HttpStatus.BAD_REQUEST);
        }

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(accountType)
                .balance(request.getInitialBalance())
                .transactions(new ArrayList<>())
                .user(user)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(request.getInitialBalance())
                .toAccountNum(null)
                .fromAccountNum(null)
                .transactionType(TransactionType.DEPOSIT)
                .build();

        account.getTransactions().add(transaction);

        Account account1 = accountRepo.save(account);

        AccountResponseDto responseDto = new AccountResponseDto();

        responseDto.setAccountNumber(account1.getAccountNumber());
        responseDto.setAccountType(account1.getAccountType());
        responseDto.setBalance(account1.getBalance());

        List<TransactionDTO> transactionDTOList = new ArrayList<>();

        for(Transaction t : account1.getTransactions()){
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .transactionID(t.getTransactionId())
                    .createdAt(t.getCreatedAt())
                    .accountNumber(t.getAccount().getAccountNumber())
                    .toAccountNum(t.getToAccountNum())
                    .fromAccountNum(t.getFromAccountNum())
                    .transactionType(t.getTransactionType())
                    .amount(t.getAmount())
                    .build();

            transactionDTOList.add(transactionDTO);
        }

        responseDto.setTransactions(transactionDTOList);

        return new ResponseEntity<>(new ApiResponse<>(true,"Account Created SuccessFully",responseDto),HttpStatus.OK);

    }

    private String generateAccountNumber(){

        String prefix = "KORAT";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = accountRepo.countAll();
        String sequence = String.format("%03d",count+1);

        return prefix + datePart + sequence;
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> withDrawMoney(@Valid WithDrawRequestDTO withDrawRequest) {

        Account account = accountRepo.findByAccountNumber(withDrawRequest.getAccountNumber());

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Account Not Found",null),HttpStatus.BAD_REQUEST);
        }

        if(account.getBalance().compareTo(withDrawRequest.getAmount()) < 0){
            return new ResponseEntity<>(new ApiResponse<>(false,"Insufficient balance!",null),HttpStatus.BAD_REQUEST);
        }

        account.setBalance(account.getBalance().subtract(withDrawRequest.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.WITHDRAW)
                .amount(withDrawRequest.getAmount())
                .account(account)
                .toAccountNum(null)
                .fromAccountNum(null)
                .build();


        account.getTransactions().add(transaction);

        Transaction transaction1 = transactionRepo.save(transaction);

        accountRepo.save(account);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionID(transaction1.getTransactionId())
                .createdAt(transaction1.getCreatedAt())
                .accountNumber(transaction1.getAccount().getAccountNumber())
                .toAccountNum(transaction1.getToAccountNum())
                .fromAccountNum(transaction1.getFromAccountNum())
                .transactionType(transaction1.getTransactionType())
                .amount(transaction1.getAmount())
                .build();

        return new ResponseEntity<>(new ApiResponse<>(true,"Withdrawal successful!",transactionDTO),HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> depositMoney(@Valid DepositRequestDTO depositRequest) {

        Account account = accountRepo.findByAccountNumber(depositRequest.getAccountNumber());

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Account Not Found",null),HttpStatus.BAD_REQUEST);
        }

        account.setBalance(account.getBalance().add(depositRequest.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .amount(depositRequest.getAmount())
                .account(account)
                .toAccountNum(null)
                .fromAccountNum(null)
                .build();

        account.getTransactions().add(transaction);

        Transaction transaction1 = transactionRepo.save(transaction);

        accountRepo.save(account);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionID(transaction1.getTransactionId())
                .createdAt(transaction1.getCreatedAt())
                .accountNumber(transaction1.getAccount().getAccountNumber())
                .toAccountNum(transaction1.getToAccountNum())
                .fromAccountNum(transaction1.getFromAccountNum())
                .transactionType(transaction1.getTransactionType())
                .amount(transaction1.getAmount())
                .build();

        return new ResponseEntity<>(new ApiResponse<>(true,"deposit successful!",transactionDTO),HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> transferMoney(TransferRequestDTO transferRequest) {

        Account senderAccount = accountRepo.findByAccountNumber(transferRequest.getSenderAccountNumber());

        if(senderAccount == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Sender Account Not Found",null),HttpStatus.BAD_REQUEST);
        }

        Account receiverAccount = accountRepo.findByAccountNumber(transferRequest.getReceiverAccountNumber());

        if(receiverAccount == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Receiver Account Not Found",null),HttpStatus.BAD_REQUEST);
        }

        if(senderAccount.getBalance().compareTo(transferRequest.getAmount()) < 0){
            return new ResponseEntity<>(new ApiResponse<>(false,"Insufficient balance!",null),HttpStatus.BAD_REQUEST);
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequest.getAmount()));

        receiverAccount.setBalance(receiverAccount.getBalance().add(transferRequest.getAmount()));

        Transaction senderTransaction = Transaction.builder()
                .transactionType(TransactionType.DEBIT)
                .amount(transferRequest.getAmount())
                .account(senderAccount)
                .toAccountNum(receiverAccount.getAccountNumber())
                .fromAccountNum(senderAccount.getAccountNumber())
                .build();

        Transaction receiverTransaction = Transaction.builder()
                .transactionType(TransactionType.CREDIT)
                .amount(transferRequest.getAmount())
                .account(receiverAccount)
                .toAccountNum(receiverAccount.getAccountNumber())
                .fromAccountNum(senderAccount.getAccountNumber())
                .build();

        senderAccount.getTransactions().add(senderTransaction);
        receiverAccount.getTransactions().add(receiverTransaction);

        Transaction transaction1 = transactionRepo.save(senderTransaction);
        transactionRepo.save(receiverTransaction);

        accountRepo.save(senderAccount);
        accountRepo.save(receiverAccount);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionID(transaction1.getTransactionId())
                .createdAt(transaction1.getCreatedAt())
                .accountNumber(transaction1.getAccount().getAccountNumber())
                .toAccountNum(transaction1.getToAccountNum())
                .fromAccountNum(transaction1.getFromAccountNum())
                .transactionType(transaction1.getTransactionType())
                .amount(transaction1.getAmount())
                .build();

        return new ResponseEntity<>(new ApiResponse<>(true,"Transfer successful!",transactionDTO),HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<?>> getAccount(String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Account Not Found",null),HttpStatus.BAD_REQUEST);
        }

        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .transactions(null)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(true,"Success",accountResponseDto),HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<?>> getBalance(String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Account Not Found",null),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ApiResponse<>(true,"Success",account.getBalance()),HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<?>> getTransaction(String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);

        if(account == null){
            return new ResponseEntity<>(new ApiResponse<>(false,"Account Not Found",null),HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>(new ApiResponse<>(true,"Success",transactionDTOList),HttpStatus.OK);

    }
}
