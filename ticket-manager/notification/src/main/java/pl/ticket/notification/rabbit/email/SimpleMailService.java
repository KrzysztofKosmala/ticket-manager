package pl.ticket.notification.rabbit.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EmailMessage;

@Service
@Slf4j
public class SimpleMailService implements EmailSender {
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String projectMail;

    public SimpleMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(EmailMessage message) {
        log.info("Wysyłanie e-mail...");
        log.info("Temat: " + message.getSubject());
        log.info("Do: " + message.getTo());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(message.getTo());
            helper.setSubject(message.getSubject());
            helper.setText(message.getBody(), true);
            helper.setFrom(projectMail);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
