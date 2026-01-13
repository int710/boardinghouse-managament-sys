package com.ptpmud.quanlynhatro.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.io.ByteArrayInputStream;

/**
 * Dịch vụ gửi email đơn giản dùng SMTP. Cấu hình qua biến môi trường:
 * SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS, SMTP_SSL=true/false
 */
public class EmailService {

    private Session buildSession() {
        String host = System.getenv().getOrDefault("SMTP_HOST", "smtp.gmail.com");
        String port = System.getenv().getOrDefault("SMTP_PORT", "587");
        String user = System.getenv().getOrDefault("SMTP_USER", "quanitwebsite@gmail.com");
        String pass = System.getenv().getOrDefault("SMTP_PASS", "glqp usiu pteg kgfv");
        boolean ssl = Boolean.parseBoolean(System.getenv().getOrDefault("SMTP_SSL", "false"));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(!ssl));
        if (ssl) {
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    public boolean send(String to, String subject, String content) {
        try {
            Session session = buildSession();
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(System.getenv().getOrDefault("SMTP_FROM", System.getenv().getOrDefault("SMTP_USER", "no-reply@example.com"))));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(content);
            Transport.send(msg);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean sendHtml(String to, String subject, String htmlContent) {
        try {
            Session session = buildSession();
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(System.getenv().getOrDefault("SMTP_FROM", System.getenv().getOrDefault("SMTP_USER", "no-reply@example.com"))));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(msg);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean sendWithAttachment(String to, String subject, String content, byte[] data, String filename) {
        try {
            Session session = buildSession();
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(System.getenv().getOrDefault("SMTP_FROM", System.getenv().getOrDefault("SMTP_USER", "no-reply@example.com"))));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(content);

            MimeBodyPart attach = new MimeBodyPart();
            attach.setFileName(filename);
            attach.setDataHandler(new javax.activation.DataHandler(new javax.activation.DataSource() {
                @Override public java.io.InputStream getInputStream() { return new ByteArrayInputStream(data); }
                @Override public java.io.OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
                @Override public String getContentType() { return "application/pdf"; }
                @Override public String getName() { return filename; }
            }));

            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            mp.addBodyPart(attach);
            msg.setContent(mp);

            Transport.send(msg);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean sendHtmlWithAttachment(String to, String subject, String htmlContent, byte[] data, String filename) {
        try {
            Session session = buildSession();
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(System.getenv().getOrDefault("SMTP_FROM", System.getenv().getOrDefault("SMTP_USER", "no-reply@example.com"))));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

            MimeBodyPart attach = new MimeBodyPart();
            attach.setFileName(filename);
            attach.setDataHandler(new javax.activation.DataHandler(new javax.activation.DataSource() {
                @Override public java.io.InputStream getInputStream() { return new ByteArrayInputStream(data); }
                @Override public java.io.OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
                @Override public String getContentType() { return "application/pdf"; }
                @Override public String getName() { return filename; }
            }));

            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);
            mp.addBodyPart(attach);
            msg.setContent(mp);

            Transport.send(msg);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

