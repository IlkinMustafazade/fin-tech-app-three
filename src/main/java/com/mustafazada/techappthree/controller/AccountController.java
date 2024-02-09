package com.mustafazada.techappthree.controller;

import com.mustafazada.techappthree.dto.request.AccountToAccountRequestDTO;
import com.mustafazada.techappthree.service.AccountService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/account")
    public ResponseEntity<?> getAccount() {
        return new ResponseEntity<>(accountService.getAccount(), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> amountTransfer(@RequestBody AccountToAccountRequestDTO accountToAccountRequestDTO){
        return new ResponseEntity<>(accountService.account2account(accountToAccountRequestDTO), HttpStatus.OK);
    }
}
