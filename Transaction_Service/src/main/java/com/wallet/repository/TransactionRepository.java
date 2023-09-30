package com.wallet.repository;

import com.wallet.model.Transaction;
import com.wallet.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Transactional
    @Modifying
    @Query("update Transaction t set t.transactionStatus= ?2 where t.TxnId= ?1")
    public void updateTransaction(String TxnId, TransactionStatus transactionStatus);

}
