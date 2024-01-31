package com.mustafazada.techappthree.service;

import com.mustafazada.techappthree.dto.request.UserRequestDTO;
import com.mustafazada.techappthree.dto.response.CommonResponseDTO;
import com.mustafazada.techappthree.dto.response.Status;
import com.mustafazada.techappthree.dto.response.StatusCode;
import com.mustafazada.techappthree.dto.response.UserResponseDTO;
import com.mustafazada.techappthree.entity.TechUser;
import com.mustafazada.techappthree.exception.UserAlreadyExist;
import com.mustafazada.techappthree.repository.UserRepository;
import com.mustafazada.techappthree.util.DTOUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    @Autowired
    DTOUtil dtoUtil;

    @Autowired
    UserRepository userRepository;

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
                .password(userRequestDTO.getPassword())
                .pin(userRequestDTO.getPin())
                .role("ROLE_USER").build();
        user.addAccountToUser(userRequestDTO.getAccountRequestDTOList());

        return CommonResponseDTO.builder().status(Status.builder()
                        .statusCode(StatusCode.SUCCESS)
                        .message("User created SUCCESSFULLY")
                .build()).data(UserResponseDTO.entityResponse(userRepository.save(user))).build();
    }
}
