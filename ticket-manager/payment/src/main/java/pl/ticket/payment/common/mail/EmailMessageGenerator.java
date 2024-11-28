package pl.ticket.payment.common.mail;


import pl.ticket.dto.EmailMessage;
import pl.ticket.dto.OrderEvent;
import pl.ticket.dto.OrderRowDto;

public class EmailMessageGenerator
{
    public static EmailMessage payOrderMessage(OrderEvent order, String paymentUrl)
    {
        return EmailMessage.builder()
                .to(order.getClientEmail())
                .subject("Płatność za zamoówienie nr " + order.getOrderId())
                .body(buildPaymentEmailBody(order, paymentUrl))
                .build();
    }

    private static String buildPaymentEmailBody(OrderEvent order, String paymentUrl) {
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
                .append("<div class='header'>Twoja płatność za zamówienie</div>")
                .append("<p>Drogi Kliencie,</p>")
                .append("<p>Dziękujemy za Twoje zamówienie. Poniżej znajdziesz link do płatności:</p>")
                .append("<a href='").append(paymentUrl).append("/pay'>").append("Opłać zamówienie!").append("</a>")
                .append("<p>Jeżeli chcesz anulować płatność kliknij w link poniżej</p>")
                .append("<a href='").append(paymentUrl).append("/reject'>").append("Anuluj zamówienie!").append("</a></br>");

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
}
