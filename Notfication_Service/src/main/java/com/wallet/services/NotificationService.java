package com.wallet.services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;
    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction_completed";

    @KafkaListener(topics = TRANSACTION_COMPLETED_TOPIC, groupId = "jbdl50")
    public void notify(String msg) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(msg);
        String TransactionId = (String) object.get("TransactionId");
        String TransactionStatus = (String) object.get("TransactionStatus");
        Long amount = (Long) object.get("amount");
        String senderMail = (String) object.get("senderMail");
        String receiverMail = (String) object.get("receiverMail");

        System.out.println(senderMail);
        System.out.println(receiverMail);

        String senderMessage = getSenderMessage(TransactionStatus, amount, TransactionId, receiverMail);


        if (senderMessage != null && senderMessage.length() > 0) {
            simpleMailMessage.setTo(senderMail);
            simpleMailMessage.setFrom("tahak2306@gmail.com");
            simpleMailMessage.setSubject("Wallet Update");
            simpleMailMessage.setText(senderMessage);
            javaMailSender.send(simpleMailMessage);
        }

        String receiverMessage = getReceiverMessage(senderMail, amount, TransactionId);

        if (receiverMessage != null && receiverMessage.length() > 0) {
            simpleMailMessage.setTo(receiverMail);
            simpleMailMessage.setFrom("tahak2306@gmail.com");
            simpleMailMessage.setSubject("Wallet Update");
            simpleMailMessage.setText(receiverMessage);
            javaMailSender.send(simpleMailMessage);
        }
    }

    public String getSenderMessage(String TransactionStatus, Long amount, String TransactionId, String receiverMail) {
        String msg = "";
        if (TransactionStatus.equals("FAILED")) {
            msg = "Hi your transaaction of amount Rs." + amount + "having TransactionId:" + TransactionId +
                    "is Failed";
            return msg;
        } else if (TransactionStatus.equals("SUCCESSFULL")) {
            msg = "Rs." + amount + " is sent to " + receiverMail + "  successfully ,TransactionId: " + TransactionId;
            return msg;
        }
        return msg;
    }

    public String getReceiverMessage(String senderId, Long amount, String TransactionId) {
        String msgg = "";
        msgg = "Hi you have received Rs. " + amount + " from " + senderId + " having TransactionId: " + TransactionId;
        return msgg;
    }

}
