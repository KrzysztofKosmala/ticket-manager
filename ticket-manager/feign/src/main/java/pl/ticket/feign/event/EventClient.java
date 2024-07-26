package pl.ticket.feign.event;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("event")
public interface EventClient
{
    @GetMapping("api/v1/events/capacity-check/{eventId}")
    CapacityCheckResponse capacityCheck(@PathVariable("eventId") Integer eventId);

}
