package com.example.notificationService.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationEvent {

    private String email;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String bankName = "NOVA BANK";

}
