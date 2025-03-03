package dev.gagnon.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private int mailPort;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.protocol}")
    private String mailProtocol;

    @Value("${mail.properties.mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.properties.mail.smtp.ssl.enable}")
    private String smtpSslEnable;

    @Value("${mail.properties.mail.smtp.socketFactory.port}")
    private String socketFactoryPort;

    @Value("${mail.properties.mail.smtp.socketFactory.class}")
    private String socketFactoryClass;
    @Value("${mailgun.sender}")
    @Getter
    private String senderEmail;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailProtocol);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.ssl.enable", smtpSslEnable);
        props.put("mail.smtp.socketFactory.port", socketFactoryPort);
        props.put("mail.smtp.socketFactory.class", socketFactoryClass);

        return mailSender;
    }
}