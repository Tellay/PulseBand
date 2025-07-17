package com.pulseband.pulseband.services;

import com.pulseband.pulseband.email.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {
    EmailConfig emailConfig = new EmailConfig();

    private Session createEmailSession() {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailConfig.getEmailHost());
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getEmailUsername(), emailConfig.getEmailPassword());
            }
        });
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(createEmailSession());
            message.setFrom(new InternetAddress(emailConfig.getEmailFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
