package pl.ticket.notification.rabbit.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EmailMessage;


@Service
@Slf4j
public class FakeEmailService implements EmailSender
{
    @Override
    public void send(EmailMessage message)
    {
        log.info("Sending email");
    }
}
