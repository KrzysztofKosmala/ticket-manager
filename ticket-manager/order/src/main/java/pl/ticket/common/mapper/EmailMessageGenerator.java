package pl.ticket.common.mapper;

import pl.ticket.common.model.dto.OrderDto;
import pl.ticket.dto.CartSummaryDto;
import pl.ticket.dto.CartSummaryItemDto;
import pl.ticket.dto.EmailMessage;
import pl.ticket.dto.TicketWithDetailsDto;

import java.util.List;

public class EmailMessageGenerator
{
    /*TODO: poprawić to generowanie ładnych maili*/
    public static EmailMessage orderCreatedMessage(OrderDto order, List<TicketWithDetailsDto> orderedTickets)
    {
        /*TODO dodać ładne maile*/
        return EmailMessage.builder()
                .to(order.getEmail())
                .subject("Twoje zamówienie zostało przyjęte.")
                .body(orderedTickets.toString())
                .build();
    }

    private static String formatEmailBody(CartSummaryDto cart) {
        StringBuilder body = new StringBuilder();
        body.append("<html><body>");
        body.append("<h2>Twoje zamówienie zostało przyjęte</h2>");
        body.append("<p>Dziękujemy za złożenie zamówienia! Poniżej znajdują się szczegóły:</p>");

        body.append("<table style='width:100%; border-collapse: collapse;'>");
        body.append("<tr><th style='border: 1px solid #dddddd; padding: 8px;'>Produkt</th>")
                .append("<th style='border: 1px solid #dddddd; padding: 8px;'>Ilość</th>")
                .append("<th style='border: 1px solid #dddddd; padding: 8px;'>Cena jednostkowa</th>")
                .append("<th style='border: 1px solid #dddddd; padding: 8px;'>Wartość</th></tr>");

        for (CartSummaryItemDto item : cart.getItems()) {
            body.append("<tr>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
                    //.append(item.getProduct().))
                    .append("</td>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
                    .append(item.getQuantity())
                    .append("</td>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
                    .append(item.getProduct().getPrice().toString())
                    .append(" PLN</td>")
                    .append("<td style='border: 1px solid #dddddd; padding: 8px;'>")
                    .append(item.getLineValue().toString())
                    .append(" PLN</td>")
                    .append("</tr>");
        }

        body.append("</table>");

        body.append("<h3>Podsumowanie:</h3>");
        body.append("<p><strong>Całkowita wartość zamówienia: </strong>")
                .append(cart.getSummary().getGrossValue().toString())
                .append(" PLN</p>");

        body.append("<p>Jeżeli masz jakieś pytania, skontaktuj się z nami.</p>");
        body.append("<p>Pozdrawiamy,<br>Zespół Sklepu</p>");
        body.append("</body></html>");

        return body.toString();
    }
}
