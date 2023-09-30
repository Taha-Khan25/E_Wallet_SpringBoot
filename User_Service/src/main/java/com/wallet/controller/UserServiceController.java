package com.wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wallet.dto.CreateUserRequest;
import com.wallet.dto.GetUserResponse;
import com.wallet.model.User;
import com.wallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserServiceController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public void createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws JsonProcessingException {
        userService.CreateUser(createUserRequest.build());
    }

    @GetMapping("/user/{userId}")
    public GetUserResponse getUser(@PathVariable("userId") int userId) throws Exception {
        User user = userService.findUser(userId);
        return GetUserResponse.userResponse(user);
    }

    @GetMapping("/user/phone/{phone}")
    public User getUserByPhone(@PathVariable("phone") String phone) {
        return userService.findUserByPhone(phone);
    }
}

