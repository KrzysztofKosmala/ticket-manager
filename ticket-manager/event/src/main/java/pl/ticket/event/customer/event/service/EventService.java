package pl.ticket.event.customer.event.service;

import org.springframework.stereotype.Service;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.feign.event.CapacityCheckResponse;

@Service
public record EventService(EventRepository eventRepository)
{


    public CapacityCheckResponse checkCapacity(Integer eventId)
    {
        return new CapacityCheckResponse(eventRepository.hasAvailableCapacity(eventId));
    }
}
