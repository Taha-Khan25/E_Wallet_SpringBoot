package com.wallet.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wallet.dto.TransactionCreateRequest;
import com.wallet.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transact")
    public String transact(@RequestBody @Valid TransactionCreateRequest transactionCreateRequest) throws JsonProcessingException {
        return transactionService.doTransaction(transactionCreateRequest);
    }
}
