package com.pulseband.pulseband.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailHandler {
    String recipient;
    String sender;

    final String host = "smtp.gmail.com";
    final String user = "pulse.band.com@gmail.com";
    final String password = "culj tmuj onnq dehu";

    public EmailHandler(String recipient, String sender) {
        this.recipient = recipient;
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void sendEmail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("BPM WARNING");
            message.setText("NIGGAS IN PARIS");

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}