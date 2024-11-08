package pl.ticket.notification.rabbit.email;


import pl.ticket.dto.EmailMessage;

public interface EmailSender
{
    void send(EmailMessage message);
}