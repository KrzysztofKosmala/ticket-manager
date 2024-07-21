package pl.ticket;

import org.springframework.stereotype.Service;
import pl.ticket.feign.event.EventClient;

import java.util.List;

@Service
public record BookingService(BookingRepository bookingRepository, EventClient eventClient)
{

    public void book(BookingRequest bookingRequest)
    {

        validateBookingRequest(bookingRequest, eventClient);
        List<Booking> bookingList = bookingRequest.ticketRequests()
                .stream()
                .map
                        (
                                ticketRequest -> Booking.builder()
                                        .eventId(ticketRequest.eventId())
                                        .attendee(ticketRequest.attendee())
                                        .build()
                        )
                .toList();



        bookingRepository.saveAll(bookingList);
    }

    private void validateBookingRequest(BookingRequest bookingRequest, EventClient eventClient)
    {

        bookingRequest.ticketRequests().forEach
                (
                        ticketRequest ->
                        {
                            if(!eventClient().capacityCheck(ticketRequest.eventId()).hasAvailableCapacity()){
                                throw new IllegalStateException("no space left to make booking");
                            }
                        }
                );
    }
}
