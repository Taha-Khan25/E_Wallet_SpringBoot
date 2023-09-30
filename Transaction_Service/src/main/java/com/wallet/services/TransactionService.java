package com.wallet.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.dto.TransactionCreateRequest;
import com.wallet.model.Transaction;
import com.wallet.model.TransactionStatus;
import com.wallet.repository.TransactionRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    TransactionRepository transactionRepository;

    private static final String TRANSACTION_CREATION_TOPIC = "transaction_created";

    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction_completed";

    private static final String WALLET_UPDATED_TOPIC = "wallet_updated";

    private static final String WALLET_UPDATED_SUCCESS_STATUS = "SUCCESS";

    private static final String WALLET_UPDATED_FAILED_STATUS = "FAILED";

    RestTemplate restTemplate = new RestTemplate();

    ObjectMapper objectMapper = new ObjectMapper();

    public String doTransaction(TransactionCreateRequest transactionCreateRequest) throws JsonProcessingException {

        Transaction transaction = Transaction.builder()
                .TxnId(UUID.randomUUID().toString())
                .senderId(transactionCreateRequest.getSenderId())
                .receiverId(transactionCreateRequest.getReceiverId())
                .amount(transactionCreateRequest.getAmount())
                .reason(transactionCreateRequest.getReason())
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        JSONObject object = new JSONObject();
        object.put("TxnId", transaction.getTxnId());
        object.put("senderId", transaction.getSenderId());
        object.put("receiverId", transaction.getReceiverId());
        object.put("amount", transaction.getAmount());

        kafkaTemplate.send(TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(object));
        transactionRepository.save(transaction);

        return transaction.getTxnId();
    }

    @KafkaListener(topics = {WALLET_UPDATED_TOPIC}, groupId = "jbdl50")
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject object = (JSONObject) new JSONParser().parse(msg);

        String TxnId = (String) object.get("TransactionId");
        String senderWalletId = (String) object.get("senderId");
        String receiverWalletId = (String) object.get("receiverId");
        Long amount = (Long) object.get("amount");
        String WalletUpdateStatus = (String) object.get("status");

        TransactionStatus transactionStatus;

        if (WalletUpdateStatus == WALLET_UPDATED_FAILED_STATUS) {
            transactionStatus = TransactionStatus.FAILED;
            transactionRepository.updateTransaction(TxnId, transactionStatus);
        } else {
            transactionStatus = TransactionStatus.SUCCESSFULL;
            transactionRepository.updateTransaction(TxnId, transactionStatus);
        }

        JSONObject senderObj = this.restTemplate
                .getForObject("http://localhost:9000/user/phone/" + senderWalletId, JSONObject.class);
        JSONObject receiverObj = this.restTemplate
                .getForObject("http://localhost:9000/user/phone/" + receiverWalletId, JSONObject.class);

        String senderMail = senderObj == null ? null : (String) senderObj.get("email");
        String receiverMail = receiverObj == null ? null : (String) receiverObj.get("email");


        object = new JSONObject();
        object.put("TransactionId", TxnId);
        object.put("TransactionStatus", transactionStatus.toString());
        object.put("amount", amount);
        object.put("senderMail", senderMail);
        object.put("senderPhone", senderWalletId);
        object.put("receiverMail", receiverMail);
        object.put("receiverPhone", receiverWalletId);

        kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC, objectMapper.writeValueAsString(object));


    }
}
