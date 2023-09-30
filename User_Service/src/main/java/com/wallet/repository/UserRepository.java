package com.wallet.repository;

import com.wallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {

    User findByPhone(String phone);
}
