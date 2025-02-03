package pl.ticket.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConcreteTicketDto
{
    private Long id;
    private byte[] qrCode;
    private Long generalTicketId;
    private Long orderRowId;
    private String title;
    private String date;
    private String time;
    private String description;

    public ConcreteTicketDto(Long id,Long generalTicketId,String title, LocalDate date, LocalTime time, String description, byte[] qrCode, Long orderRowId)
    {
        this.id = id;
        this.generalTicketId = generalTicketId;
        this.title = title;

        // Formatowanie daty na "dd.MM.yyyy"
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.date = date.format(dateFormatter);

        // Formatowanie czasu na "HH:mm"
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        this.time = time.format(timeFormatter);
        this.description = description;
        this.qrCode = qrCode;
        this.orderRowId = orderRowId;
    }


    @Override
    public String toString() {
        return "ConcreteTicketDto {\n" +
                "  id=" + id + "\n" +
                "  generalTicketId=" + generalTicketId + "\n" +
                "  title='" + title + "'\n" +
                "  date='" + date + "'\n" +
                "  time='" + time + "'\n" +
                "  description='" + description + "'\n" +
                "  orderRowId=" + orderRowId + "\n" +
                "}";
    }
}
