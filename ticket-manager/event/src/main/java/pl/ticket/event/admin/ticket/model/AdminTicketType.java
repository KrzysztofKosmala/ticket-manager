package pl.ticket.event.admin.ticket.model;

public enum AdminTicketType
{
    FULL_PRICE("full_price"),
    HALF_PRICE("half_price");
    private final String type;

    AdminTicketType(String type) {
        this.type = type;
    }
    public String getType() {

        return this.type;
    }
}
