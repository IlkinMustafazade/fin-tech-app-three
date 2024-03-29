package com.mustafazada.techappthree.exception;

import com.mustafazada.techappthree.dto.response.CommonResponseDTO;
import com.mustafazada.techappthree.dto.response.Status;
import com.mustafazada.techappthree.dto.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> internalError(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>
                (
                        CommonResponseDTO.builder().status(Status.builder()
                                .statusCode(StatusCode.INTERNAL_ERROR)
                                .message("Internal Error").build()).build(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
    }

    @ExceptionHandler(value = InvalidDTO.class)
    public ResponseEntity<?> invalidDTO(InvalidDTO invalidDTO) {
        return new ResponseEntity<>(invalidDTO.getResponseDTO(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserAlreadyExist.class)
    public ResponseEntity<?> userExist(UserAlreadyExist userAlreadyExist) {
        return new ResponseEntity<>(userAlreadyExist.getResponseDTO(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = NoSuchUserExist.class)
    public ResponseEntity<?> noFoundUser(NoSuchUserExist noSuchUserExist) {
        return new ResponseEntity<>(noSuchUserExist.getResponseDTO(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NoActiveAccount.class)
    public ResponseEntity<?> noFoundActiveAccount(NoActiveAccount noActiveAccount) {
        return new ResponseEntity<>(noActiveAccount.getResponseDTO(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidToken.class)
    public ResponseEntity<?> tokenIsNotValid(InvalidToken invalidToken) {
        return new ResponseEntity<>(invalidToken.getResponseDTO(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = CbarRestException.class)
    public ResponseEntity<?> cbarRestError(CbarRestException cbarRestException) {
        return new ResponseEntity<>(cbarRestException.getResponseDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
