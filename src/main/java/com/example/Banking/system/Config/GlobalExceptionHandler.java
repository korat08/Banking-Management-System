package com.example.Banking.system.Config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.RuntimeErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e){

        String errorMessage =e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError ->  fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(" , "));


        Map<String,Object> error = new HashMap<>();

        error.put("error " , true);
        error.put("Status ", HttpStatus.BAD_REQUEST.value());
        error.put("Message ",errorMessage);

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeErrorException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException e){

        Map<String,Object> error = new HashMap<>();

        error.put("error " , true);
        error.put("Status ", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("Message ", e.getMessage());

        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleException(Exception e) {

        Map<String, Object> error = new HashMap<>();

        error.put("error", true);
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("message", "Internal Server Error: " + e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
