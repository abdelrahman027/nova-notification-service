package com.example.notificationService.kafka.dto;


import com.example.notificationService.enums.transaction.Currency;
import com.example.notificationService.enums.transaction.TransactionDirection;

import com.example.notificationService.enums.transaction.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceUpdateEvent {

    public String accountNumber;
    public BigDecimal amount;
    public TransactionDirection transactionDirection;
    public TransactionStatus transactionStatus;
    public String reference;
    public Currency currency;
    public String email;
    public String firstName;
    public BigDecimal currentBalance;



}
