package com.wallet.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.model.User;
import com.wallet.repository.UserCacheRepository;
import com.wallet.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String USER_CREATE_TOPIC = "user_created";
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCacheRepository userCacheRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    public void CreateUser(User user) throws JsonProcessingException {
        userRepository.save(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", user.getPhone());
        jsonObject.put("UserId", user.getUserId());
        jsonObject.put("Email", user.getEmail());

        kafkaTemplate.send(USER_CREATE_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }

    public User findUser(int userId) throws Exception {
        User user = userCacheRepository.get(userId);
        if (user != null) {
            return user;
        } else {
            user = userRepository.findById(userId).orElseThrow(() -> new Exception());
            userCacheRepository.set(user);
            return user;
        }
    }

    public User findUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
}