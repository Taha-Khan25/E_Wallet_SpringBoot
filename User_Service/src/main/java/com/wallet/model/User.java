package com.wallet.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer UserId;

    private String name;

    @Column(unique = true)
    private String email;


    private String phone;

    private int age;

    @CreationTimestamp
    private Date created_on;

    @UpdateTimestamp
    private Date updated_on;

}
