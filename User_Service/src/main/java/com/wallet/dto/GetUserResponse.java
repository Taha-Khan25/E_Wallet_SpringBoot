package com.wallet.dto;

import com.wallet.model.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserResponse {

    private Integer UserId;
    private String name;
    private String email;
    private String phone;
    private int age;
    private Date created_on;
    private Date updated_on;

    public static GetUserResponse userResponse(User user) {
        return GetUserResponse.builder()
                .UserId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .age(user.getAge())
                .created_on(user.getCreated_on())
                .updated_on(user.getUpdated_on())
                .build();
    }

}
