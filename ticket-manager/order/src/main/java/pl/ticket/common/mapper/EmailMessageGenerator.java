package pl.ticket.common.mapper;

import pl.ticket.dto.*;

public class EmailMessageGenerator
{
    public static EmailMessage orderPaidMessage(OrderEvent order)
    {
        return EmailMessage.builder()
                .to(order.getClientEmail())
                .subject("Twoje zamówienie zostało opłacone.")
                .body(buildOrderPaidEmailBody(order))
                .build();
    }

    private static String buildOrderPaidEmailBody(OrderEvent order) {
        StringBuilder body = new StringBuilder();

        body.append("<!DOCTYPE html>")
                .append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; line-height: 1.6; }")
                .append(".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; }")
                .append(".header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; font-size: 20px; }")
                .append(".order-summary { margin-top: 20px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }")
                .append("th, td { text-align: left; padding: 8px; border: 1px solid #ddd; }")
                .append("th { background-color: #f4f4f4; }")
                .append(".footer { margin-top: 20px; font-size: 12px; color: #666; text-align: center; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<div class='header'>Twoje zamówienie zostało opłacone</div>")
                .append("<p>Drogi Kliencie,</p>")
                .append("<p>Dziękujemy za Twoje zamówienie. Poniżej znajdziesz jego szczegóły:</p>")
                .append("<div class='order-summary'>")
                .append("<strong>ID Zamówienia: </strong>").append(order.getOrderId()).append("<br>");

        body.append("<table>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>Produkt</th>")
                .append("<th>Opis</th>")
                .append("<th>Ilość</th>")
                .append("<th>Cena</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        for (OrderRowDto row : order.getOrderRows()) {
            body.append("<tr>")
                    .append("<td>").append(row.getProductName()).append("</td>")
                    .append("<td>").append(row.getDescription()).append("</td>")
                    .append("<td>").append(row.getQuantity()).append("</td>")
                    .append("<td>").append(row.getPrice()).append(" zł</td>")
                    .append("</tr>");
        }

        body.append("</tbody>")
                .append("</table>")
                .append("</div>")
                .append("<p>Jeśli masz jakiekolwiek pytania, skontaktuj się z nami.</p>")
                .append("<div class='footer'>Dziękujemy za zakupy w naszym sklepie!</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return body.toString();
    }

/*    public static EmailMessage orderCompletedMessage(CompleteOrderEvent orderEvent)
    {
        return EmailMessage.builder()
                .to(orderEvent.getClientEmail())
                .subject("Twoje zamówienie zostało zrealizowane.")
                .body(buildOrderCompletedEmailBody(orderEvent))
                .build();
    }*/

/*    private static String buildOrderCompletedEmailBody(CompleteOrderEvent orderEvent)
    {

    }*/

}
