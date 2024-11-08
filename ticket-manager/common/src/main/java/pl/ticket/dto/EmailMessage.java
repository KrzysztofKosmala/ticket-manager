package pl.ticket.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EmailMessage implements Serializable
{
    private String to;
    private String subject;
    private String body;
}
