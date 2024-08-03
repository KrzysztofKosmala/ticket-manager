package pl.ticket;

public enum TicketType {
    FULL_PRICE("full_price"),
    HALF_PRICE("half_price");
    private final String type;

    TicketType(String type) {
        this.type = type;
    }
    public String getType() {

        return this.type;
    }
}
