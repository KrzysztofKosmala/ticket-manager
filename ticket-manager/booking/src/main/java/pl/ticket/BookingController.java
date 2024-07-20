package pl.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/bookings")
public record BookingController(BookingService bookingService)
{
    @PostMapping
    public void registerCustomer(@RequestBody BookingRequest bookingRequest)
    {
        log.info("Booked  {}", bookingRequest);
        bookingService.book(bookingRequest);
    }
}
