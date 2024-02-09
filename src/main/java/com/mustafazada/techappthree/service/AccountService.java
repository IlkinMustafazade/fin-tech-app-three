package com.mustafazada.techappthree.service;

import com.mustafazada.techappthree.dto.request.AccountToAccountRequestDTO;
import com.mustafazada.techappthree.dto.response.*;
import com.mustafazada.techappthree.dto.response.mbdto.ValCursResponseDTO;
import com.mustafazada.techappthree.dto.response.mbdto.ValuteResponseDTO;
import com.mustafazada.techappthree.entity.Account;
import com.mustafazada.techappthree.entity.TechUser;
import com.mustafazada.techappthree.exception.InvalidDTO;
import com.mustafazada.techappthree.exception.InvalidToken;
import com.mustafazada.techappthree.repository.AccountRepository;
import com.mustafazada.techappthree.repository.UserRepository;
import com.mustafazada.techappthree.restclient.CbarRestClient;
import com.mustafazada.techappthree.util.Currency;
import com.mustafazada.techappthree.util.CurrentUser;
import com.mustafazada.techappthree.util.DTOUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountService {
    @Autowired
    DTOUtil dtoUtil;

    @Autowired
    CurrentUser currentUser;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CbarRestClient cbarRestClient;

    public CommonResponseDTO<?> getAccount() {
        Optional<TechUser> user = userRepository.findByPin(currentUser.getCurrentUser().getUsername());

        return CommonResponseDTO.builder()
                .status(Status.builder()
                        .statusCode(StatusCode.SUCCESS)
                        .message("Accounts successfully fetched")
                        .build())
                .data(AccountResponseDTOList.entityToDTO(user.get().getAccountList()))
                .build();
    }

    @Transactional
    public CommonResponseDTO<?> account2account(AccountToAccountRequestDTO accountToAccountRequestDTO) {
        validateUserAndAccounts(accountToAccountRequestDTO);
        dtoUtil.isValid(accountToAccountRequestDTO);
        if (accountToAccountRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidDTO.builder()
                    .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                            .statusCode(StatusCode.INVALID_DTO)
                            .message("Amount is not correct")
                            .build()).build()).build();
        } else if (accountToAccountRequestDTO.getDebitAccount().equals(accountToAccountRequestDTO.getCreditAccount())) {
            throw InvalidDTO.builder()
                    .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                            .statusCode(StatusCode.INVALID_DTO)
                            .message("Debit account: " + accountToAccountRequestDTO.getCreditAccount() +
                                    " and Credit account: " + accountToAccountRequestDTO.getCreditAccount() +
                                    " are same")
                            .build()).build()).build();
        }
        Optional<Account> byDebitAccountNo = accountRepository.findByAccountNo(accountToAccountRequestDTO.getDebitAccount());
        Account debitAccount;
        Account creditAccount;

        if (byDebitAccountNo.isPresent()) {
            debitAccount = byDebitAccountNo.get();
            if (!debitAccount.getIsActive()) {
                throw InvalidDTO.builder()
                        .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                                .statusCode(StatusCode.INVALID_DTO)
                                .message("Debit account is not Active")
                                .build()).build()).build();
            }
            if (debitAccount.getBalance().compareTo(accountToAccountRequestDTO.getAmount()) <= 0) {
                throw InvalidDTO.builder()
                        .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                                .statusCode(StatusCode.INVALID_DTO)
                                .message("Balance is not enough")
                                .build()).build()).build();
            }
            Optional<Account> byCreditAccountNo = accountRepository.findByAccountNo(accountToAccountRequestDTO.getCreditAccount());
            if (byCreditAccountNo.isPresent()) {
                creditAccount = byCreditAccountNo.get();
                if (!creditAccount.getIsActive()) {
                    throw InvalidDTO.builder()
                            .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                                    .statusCode(StatusCode.INVALID_DTO)
                                    .message("Credit Account is not Active")
                                    .build()).build()).build();
                }
            } else {
                throw InvalidDTO.builder()
                        .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                                .statusCode(StatusCode.INVALID_DTO)
                                .message("Credit account is not present")
                                .build()).build()).build();
            }
        } else {
            throw InvalidDTO.builder()
                    .responseDTO(CommonResponseDTO.builder().status(Status.builder()
                            .statusCode(StatusCode.INVALID_DTO)
                            .message("Debit account is not present")
                            .build()).build()).build();
        }
        if (!debitAccount.getCurrency().equals(creditAccount.getCurrency())) {
            ValCursResponseDTO currency = cbarRestClient.getCurrency();
            currency.getValTypeList().forEach(valTypeResponseDTO -> {
                List<ValuteResponseDTO> valuteList = valTypeResponseDTO.getValuteList();

                if (Objects.nonNull(valuteList) && !ObjectUtils.isEmpty(valuteList)) {
                    valuteList.stream().filter(valuteResponseDTO -> Objects.nonNull(valuteResponseDTO)
                                    && !ObjectUtils.isEmpty(valuteResponseDTO)
                                    && valuteResponseDTO.getCode().equals(debitAccount.getCurrency().toString())
                                    && debitAccount.getCurrency().equals(Currency.USD)).findFirst()
                            .ifPresent(valuteResponseDTO -> {
                                debitAccount
                                        .setBalance(debitAccount.getBalance().subtract(accountToAccountRequestDTO.getAmount()));
                                creditAccount.setBalance(creditAccount.getBalance().add(accountToAccountRequestDTO.getAmount()
                                        .multiply(valuteResponseDTO.getValue())));
                            });
                    valuteList.stream().filter(valuteResponseDTO -> Objects.nonNull(valuteResponseDTO)
                                    && !ObjectUtils.isEmpty(valuteResponseDTO)
                                    && !valuteResponseDTO.getCode().equals(debitAccount.getCurrency().toString())
                                    && valuteResponseDTO.getCode().equals(Currency.USD.toString())).findFirst()
                            .ifPresent(valuteResponseDTO -> {
                                debitAccount.setBalance
                                        (debitAccount.getBalance().subtract(accountToAccountRequestDTO.getAmount()));
                                creditAccount.setBalance(creditAccount.getBalance().add(accountToAccountRequestDTO.getAmount()
                                        .divide(valuteResponseDTO.getValue(), RoundingMode.DOWN)));
                            });
                }
            });
        } else {
            debitAccount.setBalance(debitAccount.getBalance().subtract(accountToAccountRequestDTO.getAmount()));
            creditAccount.setBalance(creditAccount.getBalance().add(accountToAccountRequestDTO.getAmount()));
        }
        return CommonResponseDTO.builder()
                .status(Status.builder()
                        .statusCode(StatusCode.SUCCESS)
                        .message("Transfer completed successfully").build())
                .data(AccountResponseDTO.builder()
                        .balance(debitAccount.getBalance())
                        .currency(debitAccount.getCurrency())
                        .isActive(debitAccount.getIsActive())
                        .accountNo(debitAccount.getAccountNo()).build()).build();
    }


    private void validateUserAndAccounts(AccountToAccountRequestDTO requestDTO) {
        Optional<TechUser> user = userRepository.findByPin(currentUser.getCurrentUser().getUsername());
        if (user.isEmpty()) {
            throw InvalidToken.builder().responseDTO(CommonResponseDTO.builder().status(Status.builder()
                    .statusCode(StatusCode.INVALID_TOKEN)
                    .message("The token is not tied to this user")
                    .build()).build()).build();
        }
        TechUser techUser = user.get();
        List<Account> accountList = techUser.getAccountList();
        if (accountList.stream().noneMatch(account -> account.getAccountNo().equals(requestDTO.getDebitAccount()))) {
            throw InvalidToken.builder().responseDTO(CommonResponseDTO.builder().status(Status.builder()
                    .statusCode(StatusCode.INVALID_TOKEN)
                    .message("The token is not tied to this user's account")
                    .build()).build()).build();
        }
    }
}
