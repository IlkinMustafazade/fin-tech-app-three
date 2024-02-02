package com.mustafazada.techappthree.controller;

import com.mustafazada.techappthree.dto.request.AuthenticationRequestDTO;
import com.mustafazada.techappthree.dto.request.UserRequestDTO;
import com.mustafazada.techappthree.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO userRequestDTO){
        return new ResponseEntity<>(userService.saveUser(userRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO authenticationRequestDTO){
        return new ResponseEntity<>(userService.loginUser(authenticationRequestDTO), HttpStatus.OK);
    }
}
