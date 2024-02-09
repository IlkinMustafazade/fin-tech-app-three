package com.mustafazada.techappthree.controller;

import com.mustafazada.techappthree.service.CurrencyService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyController {
    @Autowired
    CurrencyService currencyService;
    @GetMapping("/currency")
    public ResponseEntity<?> getCurrencyMB(){
        return new ResponseEntity<>(currencyService.getCurrencyRate(), HttpStatus.OK);
    }
}
