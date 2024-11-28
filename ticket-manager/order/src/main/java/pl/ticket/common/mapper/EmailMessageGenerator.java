package pl.ticket.common.mapper;

import pl.ticket.dto.*;

import java.util.Base64;

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

    public static EmailMessage orderRejectedMessage(OrderEvent order)
    {
        return EmailMessage.builder()
                .to(order.getClientEmail())
                .subject("Twoje zamówienie zostało anulowane.")
                .body(buildOrderRejectedEmailBody(order))
                .build();
    }

    private static String buildOrderRejectedEmailBody(OrderEvent order) {
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
                .append("<div class='header'>Twoje zamówienie zostało anulowane</div>")
                .append("<p>Drogi Kliencie,</p>")
                .append("<p>Twoje zamówienie : ").append(order.getOrderId()).append("zostało anulowane z braku płatności.</p>")
                .append("<div class='order-summary'>")
                .append("<br>");

        return body.toString();
    }

    public static EmailMessage orderCompleted(CompleteOrderEvent orderEvent)
    {
        return EmailMessage.builder()
                .to(orderEvent.getClientEmail())
                .subject("Twoje zamówienie zostało zrealizowane.")
                .body(generateOrderCompletedEmailBody(orderEvent))
                .build();
    }

    private static String generateOrderCompletedEmailBody(CompleteOrderEvent orderEvent) {
        StringBuilder emailBuilder = new StringBuilder();

        // Nagłówek wiadomości
        emailBuilder.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; }")
                .append(".container { width: 80%; margin: 0 auto; }")
                .append(".header { text-align: center; margin-bottom: 20px; }")
                .append(".tickets { border-collapse: collapse; width: 100%; margin-top: 20px; }")
                .append(".tickets th, .tickets td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append(".tickets th { background-color: #f4f4f4; }")
                .append(".qr-code { text-align: center; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<div class='header'>")
                .append("<h2>Podsumowanie zamówienia</h2>")
                .append("<p>Dziękujemy za złożenie zamówienia! Oto szczegóły:</p>")
                .append("</div>")
                .append("<p><strong>Numer zamówienia:</strong> ").append(orderEvent.getOrderId()).append("</p>")
                .append("<p><strong>Adres e-mail klienta:</strong> ").append(orderEvent.getClientEmail()).append("</p>")
                .append("<p>").append(orderEvent.getMessage()).append("</p>");

        // Tabela biletów
        emailBuilder.append("<table class='tickets'>")
                .append("<tr>")
                .append("<th>Tytuł</th>")
                .append("<th>Data</th>")
                .append("<th>Godzina</th>")
                .append("<th>Opis</th>")
                .append("<th>QR Code</th>")
                .append("</tr>");

        // Wiersze dla każdego biletu
        for (ConcreteTicketDto ticket : orderEvent.getConcreteTickets()) {
            emailBuilder.append("<tr>")
                    .append("<td>").append(ticket.getTitle()).append("</td>")
                    .append("<td>").append(ticket.getDate()).append("</td>")
                    .append("<td>").append(ticket.getTime()).append("</td>")
                    .append("<td>").append(ticket.getDescription()).append("</td>")
                    .append("<td class='qr-code'>");

            if (ticket.getQrCode() != null) {
                String base64QrCode = Base64.getEncoder().encodeToString(ticket.getQrCode());
                emailBuilder.append("<img src='data:image/png;base64,").append(base64QrCode).append("' alt='QR Code' style='width:100px;height:100px;'/>");
            } else {
                emailBuilder.append("Brak QR Code");
            }

            emailBuilder.append("</td>")
                    .append("</tr>");
        }

        // Zakończenie wiadomości
        emailBuilder.append("</table>")
                .append("<p style='margin-top: 20px;'>Dziękujemy za skorzystanie z naszych usług.</p>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return emailBuilder.toString();
    }

}
