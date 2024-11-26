package pl.ticket.notification.rabbit.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        log.info("Sending email");
        log.info("Subject: " + message.getSubject());
        log.info("To: " + message.getTo());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(message.getTo());
        simpleMailMessage.setSubject(message.getSubject());
        simpleMailMessage.setText(message.getBody());
        simpleMailMessage.setFrom(projectMail);
        mailSender.send(simpleMailMessage);
    }
}
