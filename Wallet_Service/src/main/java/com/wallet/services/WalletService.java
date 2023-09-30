package com.wallet.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.model.Wallet;
import com.wallet.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private static final String USER_CREATED_TOPIC = "user_created";
    private static final String TRANSACTION_CREATION_TOPIC = "transaction_created";

    private static final String WALLET_UPDATED_TOPIC = "wallet_updated";

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${wallet.initial.balance}")
    Long balance;

    @KafkaListener(topics = {USER_CREATED_TOPIC}, groupId = "jbdl50")
    public void createWallet(String msg) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(msg);

        String walletId = (String) object.get("phone");

        Wallet wallet = Wallet.builder()
                .walletId(walletId)
                .currency("INR")
                .balance(balance)
                .build();

        walletRepository.save(wallet);
    }

    @KafkaListener(topics = {TRANSACTION_CREATION_TOPIC}, groupId = "jbdl50")
    public void updateWallet(String msg) throws ParseException, JsonProcessingException {


        JSONObject object = (JSONObject) new JSONParser().parse(msg);
        String senderWalletId = (String) object.get("senderId");
        String receiverWalletId = (String) object.get("receiverId");
        Long amount = (Long) object.get("amount");
        String TxnId = (String) object.get("TxnId");

        try {
            Wallet senderWallet = walletRepository.findByWalletId(senderWalletId);
            Wallet receiverWallet = walletRepository.findByWalletId(receiverWalletId);

            if (senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
                object = init(senderWalletId, receiverWalletId, amount, TxnId, "FAILED");
                kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(object));
                return;
            }
            walletRepository.decrementBalance(senderWalletId, amount);
            walletRepository.incrementBalance(receiverWalletId, amount);

            object = init(senderWalletId, receiverWalletId, amount, TxnId, "SUCCESS");
            kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            object = init(senderWalletId, receiverWalletId, amount, TxnId, "FAILED");
            object.put("ErrorMessage", e.getMessage());
            kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(object));
        }
    }

    private JSONObject init(String senderId, String receiverId, Long amount, String TxnId, String status) {
        JSONObject object = new JSONObject();
        object.put("senderId", senderId);
        object.put("receiverId", receiverId);
        object.put("amount", amount);
        object.put("TransactionId", TxnId);
        object.put("status", status);
        return object;
    }
}
