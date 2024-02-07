package com.mustafazada.techappthree.repository;

import com.mustafazada.techappthree.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByAccountNo(Integer accountNo);
}
