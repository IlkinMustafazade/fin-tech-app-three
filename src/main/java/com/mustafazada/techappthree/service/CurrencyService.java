package com.mustafazada.techappthree.service;

import com.mustafazada.techappthree.dto.response.CommonResponseDTO;
import com.mustafazada.techappthree.dto.response.Status;
import com.mustafazada.techappthree.dto.response.StatusCode;
import com.mustafazada.techappthree.dto.response.mbdto.ValCursResponseDTO;
import com.mustafazada.techappthree.restclient.CbarRestClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyService {
    @Autowired
    CbarRestClient cbarRestClient;

    public CommonResponseDTO<?> getCurrencyRate(){
        ValCursResponseDTO valCursResponseDTO = cbarRestClient.getCurrency();
        return CommonResponseDTO.builder()
                .status(Status.builder()
                        .statusCode(StatusCode.SUCCESS)
                        .message("All currencies").build())
                .data(valCursResponseDTO).build();
    }
}
