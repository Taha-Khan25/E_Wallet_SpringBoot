package com.wallet.repository;

import com.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    public Wallet findByWalletId(String walletid);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance= w.balance + :amount where w.walletId = :walletId")
    public void incrementBalance(String walletId, Long amount);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance= w.balance - :amount where w.walletId = :walletId")
    public void decrementBalance(String walletId, Long amount);

}
