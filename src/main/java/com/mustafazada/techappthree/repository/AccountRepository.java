package com.mustafazada.techappthree.repository;

import com.mustafazada.techappthree.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
