package com.mustafazada.techappthree.service;

import com.mustafazada.techappthree.dto.request.AuthenticationRequestDTO;
import com.mustafazada.techappthree.dto.request.UserRequestDTO;
import com.mustafazada.techappthree.dto.response.CommonResponseDTO;
import com.mustafazada.techappthree.dto.response.Status;
import com.mustafazada.techappthree.dto.response.StatusCode;
import com.mustafazada.techappthree.dto.response.UserResponseDTO;
import com.mustafazada.techappthree.entity.TechUser;
import com.mustafazada.techappthree.exception.NoSuchUserExist;
import com.mustafazada.techappthree.exception.UserAlreadyExist;
import com.mustafazada.techappthree.repository.UserRepository;
import com.mustafazada.techappthree.util.DTOUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    @Autowired
    DTOUtil dtoUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    public CommonResponseDTO<?> saveUser(UserRequestDTO userRequestDTO){
        dtoUtil.isValid(userRequestDTO);
        if(userRepository.findByPin(userRequestDTO.getPin()).isPresent()){
            throw UserAlreadyExist.builder().responseDTO(CommonResponseDTO.builder().status(Status.builder()
                            .statusCode(StatusCode.USER_EXIST)
                            .message("User with pin: " + userRequestDTO.getPin() +
                                    " is exist. Please enter a pin that has not been registered before")
                    .build()).build()).build();
        }

        TechUser user = TechUser.builder()
                .name(userRequestDTO.getName())
                .surname(userRequestDTO.getSurname())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .pin(userRequestDTO.getPin())
                .role("ROLE_USER").build();
        user.addAccountToUser(userRequestDTO.getAccountRequestDTOList());

        return CommonResponseDTO.builder().status(Status.builder()
                        .statusCode(StatusCode.SUCCESS)
                        .message("User created SUCCESSFULLY")
                .build()).data(UserResponseDTO.entityResponse(userRepository.save(user))).build();
    }

    public CommonResponseDTO<?> loginUser(AuthenticationRequestDTO authenticationRequestDTO){
        dtoUtil.isValid(authenticationRequestDTO);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequestDTO.getPin(),
                    authenticationRequestDTO.getPassword()
            ));
        }catch (Exception e){
            throw NoSuchUserExist.builder().responseDTO(CommonResponseDTO.builder().status(Status.builder()
                    .statusCode(StatusCode.USER_NOT_EXIST)
                    .message("pin: " + authenticationRequestDTO.getPin() + " or password: " +
                            authenticationRequestDTO.getPassword() + " is wrong.")
                    .build()).build()).build();
        }
        return CommonResponseDTO.builder().data(authenticationRequestDTO).status(Status.builder()
                .statusCode(StatusCode.SUCCESS)
                .message("Welcom to our FIN-TECH Application")
                .build()).build();
    }
}
