package com.wallet.dto;

import com.wallet.model.User;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @Min(18)
    private int age;

    public User build() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .age(this.age)
                .build();
    }
}


