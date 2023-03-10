package com.example.redit_clone.service;

import com.example.redit_clone.exceptions.MyCustomException;
import com.example.redit_clone.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.logging.Logger;


@Service
@Slf4j
@AllArgsConstructor
public class MailService {


    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;



    //need to check RabbitMQ and ActiveMQ message Queues for async requests
    @Async
    public void sendEmail(NotificationEmail notificationEmail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("xavales666@gmail.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder(notificationEmail.getBody()), true);

        };
        try{
            mailSender.send(messagePreparator);
            log.info("Activation email sent");
        }catch (MailException e) {
            throw new MyCustomException("Exception occurred when sending mail to "
                    + notificationEmail.getRecipient());
        }
    }
    private String mailContentBuilder(String message) {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("message", message);


        return templateEngine.process("mailTemplate", context);
    }

}
