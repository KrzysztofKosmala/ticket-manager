package pl.ticket.notification.rabbit.email;


import pl.ticket.feign.notification.EmailMessage;

public interface EmailSender
{
    void send(EmailMessage message);
}