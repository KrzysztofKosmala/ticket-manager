package pl.ticket.customer.model;

public enum OrderStatus
{
    CREATED("Stworzone"),
    PAID("Opłacone"),
    CANCELLING("Przetwarzane"),
    RESERVED("Zarezeerwowane"),
    WAITING_FOR_DELIVERY("Czeka na dostawę"),
    COMPLETED("Zrealizowane"),
    CANCELED("Anulowane"),
    REFUND("Zwrócone");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
