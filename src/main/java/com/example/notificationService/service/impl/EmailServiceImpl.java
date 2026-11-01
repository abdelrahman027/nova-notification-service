package com.example.notificationService.service.impl;

import com.example.notificationService.entity.Notification;
import com.example.notificationService.enums.NotificationStatus;
import com.example.notificationService.enums.NotificationType;
import com.example.notificationService.enums.transaction.TransactionDirection;
import com.example.notificationService.kafka.dto.BalanceUpdateEvent;
import com.example.notificationService.kafka.dto.UserRegistrationEvent;
import com.example.notificationService.repository.NotificationRepository;
import com.example.notificationService.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final NotificationRepository notificationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendWelcomeEmail(UserRegistrationEvent event) {
        try{
            Context context = new Context();
            context.setVariable("bankName",event.getBankName());
            context.setVariable("firstName",event.getFirstName());
            context.setVariable("lastName",event.getLastName());
            context.setVariable("email",event.getEmail());
            context.setVariable("accountNumber",event.getAccountNumber());

            String htmlTemplate = templateEngine.process("welcome-email",context);
            Notification notificationToSave = Notification.builder()
                    .recipientEmail(event.getEmail())
                    .type(NotificationType.EMAIL)
                    .subject("welcome to " +event.getBankName()+ " bank")
                    .message(htmlTemplate)
                    .status(NotificationStatus.SENT)
                    .build();


            sendEmailOut(event.getEmail(), notificationToSave.getSubject(),htmlTemplate);

            notificationRepository.save(notificationToSave);
            log.info("welcome email sent successfully");

        }
        catch (Exception ex){
            log.error("error sending message {}" ,ex.getMessage());
            Notification notificationToSave = Notification.builder()
                    .recipientEmail(event.getEmail())
                    .type(NotificationType.EMAIL)
                    .subject("welcome to " +event.getBankName()+ " bank")
                    .message("failed to send email out")
                    .status(NotificationStatus.FAILED)
                    .build();
            notificationRepository.save(notificationToSave);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void sendTransactionAlertEmail(BalanceUpdateEvent event) {
        try {
            Context context = new Context();
            context.setVariable("name",event.getFirstName());
            context.setVariable("bankName","NOVA BANK");
            context.setVariable("currency","USD");
            context.setVariable("reference",event.getReference());
            context.setVariable("amount",event.getAmount());
            context.setVariable("firstName","Nova bank client");

            context.setVariable("accountNumber",event.getAccountNumber());
            context.setVariable("date", LocalDateTime.now().toString());
            context.setVariable("balance",event.getCurrentBalance());

            String templateName;
            String subject;

            if (TransactionDirection.CREDIT.equals(event.getTransactionDirection())){
                templateName = "credit-alert";
                subject ="Credit Alert :[" +event.getReference()+"]";
            } else {
                templateName = "debit-alert";
                subject ="Debit Alert :[" +event.getReference()+"]";
            }

            String emailTemplate = templateEngine.process(templateName,context);
            Notification notificationToSave = Notification.builder()
                    .recipientEmail(event.getEmail())
                    .type(NotificationType.EMAIL)
                    .subject(subject)
                    .message(emailTemplate)
                    .status(NotificationStatus.SENT)
                    .build();
            sendEmailOut(event.getEmail(),subject,emailTemplate);
            notificationRepository.save(notificationToSave);
            log.info("email of transaction sent");

        }
        catch (Exception ex){
            log.error("error sending transaction message {}" ,ex.getMessage());
            Notification notificationToSave = Notification.builder()
                    .recipientEmail(event.getEmail())
                    .type(NotificationType.EMAIL)
                    .subject("Transaction alert error")
                    .message("failed to send email out")
                    .status(NotificationStatus.FAILED)
                    .build();
            notificationRepository.save(notificationToSave);
            throw new RuntimeException(ex.getMessage());
        }
    }


    private void sendEmailOut(String to,String subject,String emailTemplate) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage ,true,"UTF-8"
        );
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(emailTemplate,true);

        mailSender.send(mimeMessage);

    }
}
