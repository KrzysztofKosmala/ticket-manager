package pl.ticket.notification.rabbit.email;


import jakarta.mail.MessagingException;
import pl.ticket.dto.EmailMessage;

public interface EmailSender
{
    void send(EmailMessage message) throws MessagingException;
}
