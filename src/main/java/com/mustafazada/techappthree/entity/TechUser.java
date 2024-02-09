package com.mustafazada.techappthree.entity;

import com.mustafazada.techappthree.dto.request.AccountRequestDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tech_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "user_name")
    String name;
    @Column(name = "user_surname")
    String surname;
    @Column(name = "password")
    String password;
    @Column(name = "pin", unique = true)
    String pin;
    @Column(name = "role")
    String role;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    List<Account> accountList;

    public void addAccountToUser(List<AccountRequestDTO> accountRequestDTOList){
        accountList = new ArrayList<>();
        accountRequestDTOList.forEach(accountDTO -> accountList.add(Account.builder()
                        .balance(accountDTO.getBalance())
                        .currency(accountDTO.getCurrency())
                        .isActive(accountDTO.getIsActive())
                        .accountNo(accountDTO.getAccountNo())
                        .user(this).build()));
    }
}
