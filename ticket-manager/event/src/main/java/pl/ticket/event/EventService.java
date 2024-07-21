package pl.ticket.event;

import org.springframework.stereotype.Service;
import pl.ticket.feign.event.CapacityCheckResponse;

@Service
public record EventService(EventRepository eventRepository)
{

    public void createEvent(EventCreationRequest eventCreationRequest)
    {
        Event event = Event.builder()
                .title(eventCreationRequest.title())
                .description(eventCreationRequest.description())
                .capacity(eventCreationRequest.capacity())
                .build();

        eventRepository.save(event);
    }

    public CapacityCheckResponse checkCapacity(Integer eventId)
    {
        return new CapacityCheckResponse(eventRepository.hasAvailableCapacity(eventId));
    }
}
