package pl.ticket.feign.notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class EmailMessage implements Serializable
{
    private String to;
    private String subject;
    private String body;
}
