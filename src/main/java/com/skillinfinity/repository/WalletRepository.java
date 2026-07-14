package com.skillinfinity.repository;

import com.skillinfinity.entity.User;
import com.skillinfinity.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);

}
