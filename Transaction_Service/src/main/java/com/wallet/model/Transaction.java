package com.wallet.model;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String TxnId;

    private String senderId;

    private String receiverId;

    @Min(1)
    private Long amount;

    @Enumerated(value = EnumType.STRING)
    private TransactionStatus transactionStatus;

    private String reason;

    @CreationTimestamp
    private Date created_on;

    @UpdateTimestamp
    private Date updated_on;


}
