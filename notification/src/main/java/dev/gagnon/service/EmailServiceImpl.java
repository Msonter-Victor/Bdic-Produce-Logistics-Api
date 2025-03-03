package dev.gagnon.service;

import dev.gagnon.config.MailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    public void sendWelcomeEmail(String to, String name) throws MessagingException {
        String subject = "Registration Successful!";
        String body = buildWelcomeEmailBody(name);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(mailConfig.getSenderEmail());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true enables HTML

        mailSender.send(message);
    }

    private String buildWelcomeEmailBody(String name) {
        return "<html>" +
                "<body>" +
                "<h2>Hello, " + name + "!</h2>" +
                "<p>We are excited to have you onboard. Get ready to explore amazing features!</p>" +
                "<p>If you have any questions, feel free to reach out.</p>" +
                "<br>" +
                "<p>Best Regards,</p>" +
                "<p><b>Produce and Logistics Support Team</b></p>" +
                "</body>" +
                "</html>";
    }
}
