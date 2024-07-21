package pl.ticket.event;

public record EventCreationRequest(String title, String description, Integer capacity) {
}
