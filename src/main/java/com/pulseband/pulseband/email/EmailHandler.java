package com.pulseband.pulseband.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailHandler {
    private final String recipient;
    private final String sender;

    private final EmailConfig emailConfig = new EmailConfig();
    private final String username = emailConfig.getEmailUser();
    private final String password = emailConfig.getEmailPassword();

    public EmailHandler(String recipient, String sender) {
        this.recipient = recipient;
        this.sender = sender;
    }

    public void sendEmail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", emailConfig.getEmailSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailConfig.getEmailSmtpStartTlsEnable());
        props.put("mail.smtp.host", emailConfig.getEmailSmtpHost());
        props.put("mail.smtp.port", emailConfig.getEmailSmtpPort());

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("DRIVER WARNING");
            message.setText("NIGGAS IN PARIS");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}