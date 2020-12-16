package com.example.monzun.services;

import com.example.monzun.entities.Mail;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final Environment environment;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender emailSender, Environment environment, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.environment = environment;
        this.templateEngine = templateEngine;
    }

    /**
     * Отправка email по указанному шаблону
     *
     * @param mail     email
     * @param template шаблон письма
     * @throws MessagingException MessagingException
     */
    public void sendEmail(Mail mail, String template) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getProps());

        String html = templateEngine.process(template, context);

        helper.setTo(mail.getMailTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }

    /**
     * Создание письма
     *
     * @param receiverMail email получателя
     * @param subject      тема письма
     * @param props        Map<String, Object> доп.данные
     * @return Mail
     */
    public Mail createMail(
            String receiverMail,
            String subject,
            Map<String, Object> props
    ) {
        Mail mail = new Mail();
        mail.setFrom(environment.getProperty("SYSTEM_EMAIL_FROM"));
        mail.setMailTo(receiverMail);
        mail.setSubject(subject);
        mail.setProps(props);

        return mail;
    }
}
