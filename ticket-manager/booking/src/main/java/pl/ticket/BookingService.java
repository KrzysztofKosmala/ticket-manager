package pl.ticket;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record BookingService(BookingRepository bookingRepository) {
    public void book(BookingRequest bookingRequest) {

        List<Booking> bookingList = bookingRequest.ticketRequests().stream().map(ticketRequest -> Booking.builder().eventId(ticketRequest.eventId()).attendee(ticketRequest.attendee()).build()).toList();
        bookingRepository.saveAll(bookingList);
    }
}
