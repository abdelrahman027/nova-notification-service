package com.example.notificationService.kafka.service;

import com.example.notificationService.kafka.dto.BalanceUpdateEvent;
import com.example.notificationService.kafka.dto.UserRegistrationEvent;
import com.example.notificationService.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerListener {
    private final EmailService emailService;

    @KafkaListener(topics = "user-registred-events",groupId = "notification-group")
    private void consumerRegisterEvent(UserRegistrationEvent event){
        log.info("received notification message");
        try {
            emailService.sendWelcomeEmail(event);
        } catch (Exception ex){
            log.error("error sending message {}" , ex.getMessage());
        }

    }

    @KafkaListener(topics = "balance-update-events",groupId = "notification-group")
    private void consumerBalanceAlert(BalanceUpdateEvent event){
        log.info("received balance notification message");
        try {
            emailService.sendTransactionAlertEmail(event);
        } catch (Exception ex){
            log.error("error balance sending message {}" , ex.getMessage());
        }

    }


}
