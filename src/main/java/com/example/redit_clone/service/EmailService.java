package com.example.redit_clone.service;
import com.example.redit_clone.exceptions.MyCustomException;
import com.example.redit_clone.model.Email;
import com.example.redit_clone.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

@Slf4j
public class EmailService {


    Environment environment;
    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;


    public EmailService(Environment environment, JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.environment = environment;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(User user, Email email) throws MessagingException, UnsupportedEncodingException {
        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper;

        mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setSubject(email.getMailSubject());
        mimeMessageHelper.setFrom(new InternetAddress(mailFrom, mailFromName));


//        final String htmlContent = this.templateEngine.process(email.getTemplateName(), );
        mimeMessageHelper.setText(mailContnentBuilder(email.getTemplateName()), true);
        mimeMessageHelper.addInline("templates/OTS-logo-moto.png",
                new File("templates/ots_logo_01.png"));


        try{
            javaMailSender.send(mimeMessage);
            log.info("Email sent");
        }catch (MailException e) {
            throw new MyCustomException("error occurred while sending the email to the " + user.getUserName());
        }

        //ClassPathResourse path = new ClassPathResource("templates/OTS-logo-moto.png");

    }

    private String mailContnentBuilder(String message) {
            Context context = new Context(LocaleContextHolder.getLocale());
            context.setVariable("message", message);
            return templateEngine.process("mailTemplate", context);
    }

}
