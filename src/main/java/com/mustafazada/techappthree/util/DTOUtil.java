package com.mustafazada.techappthree.util;

import com.mustafazada.techappthree.dto.request.AccountToAccountRequestDTO;
import com.mustafazada.techappthree.dto.request.AuthenticationRequestDTO;
import com.mustafazada.techappthree.dto.request.UserRequestDTO;
import com.mustafazada.techappthree.dto.response.CommonResponseDTO;
import com.mustafazada.techappthree.dto.response.Status;
import com.mustafazada.techappthree.dto.response.StatusCode;
import com.mustafazada.techappthree.exception.InvalidDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DTOUtil {

    @Autowired
    Logger logger;

    public void isValid(UserRequestDTO userRequestDTO){
        logger.warn(userRequestDTO.toString());
        checkDTOInput(userRequestDTO.getName());
        checkDTOInput(userRequestDTO.getSurname());
        checkDTOInput(userRequestDTO.getPassword());
        checkDTOInput(userRequestDTO.getPin());
        checkDTOInput(userRequestDTO.getAccountRequestDTOList());
    }

    public void isValid(AuthenticationRequestDTO authenticationRequestDTO){
        logger.warn(authenticationRequestDTO.toString());
        checkDTOInput(authenticationRequestDTO.getPin());
        checkDTOInput(authenticationRequestDTO.getPassword());
    }

    public void isValid(AccountToAccountRequestDTO accountToAccountRequestDTO){
        logger.warn(accountToAccountRequestDTO.toString());
        checkDTOInput(accountToAccountRequestDTO.getDebitAccount());
        checkDTOInput(accountToAccountRequestDTO.getCreditAccount());
        checkDTOInput(accountToAccountRequestDTO.getAmount());
    }

    private <T> void checkDTOInput(T t){
        if(Objects.isNull(t) || t.toString().isBlank()){
            logger.error("Invalid input");
            throw InvalidDTO.builder().responseDTO(CommonResponseDTO.builder().status(Status.builder()
                            .statusCode(StatusCode.INVALID_DTO)
                            .message("Invalid data")
                    .build()).build()).build();
        }
    }
}
