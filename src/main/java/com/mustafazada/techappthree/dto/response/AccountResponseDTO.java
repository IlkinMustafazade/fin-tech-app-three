package com.mustafazada.techappthree.dto.response;

import com.mustafazada.techappthree.entity.Account;
import com.mustafazada.techappthree.util.Currency;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    BigDecimal balance;
    Currency currency;
    Boolean isActive;
    Integer accountNo;

    public static AccountResponseDTO entityDTO(Account account) {
        return AccountResponseDTO.builder()
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .isActive(account.getIsActive())
                .accountNo(account.getAccountNo())
                .build();
    }
}