package pl.ticket.event;

import org.springframework.stereotype.Service;

@Service
public record EventService(EventRepository eventRepository)
{

    public void createEvent(EventCreationRequest eventCreationRequest)
    {
        Event event = Event.builder()
                .title(eventCreationRequest.title())
                .description(eventCreationRequest.description())
                .build();

        eventRepository.save(event);
    }
}
