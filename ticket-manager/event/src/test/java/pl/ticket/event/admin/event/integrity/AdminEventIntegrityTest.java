package pl.ticket.event.admin.event.integrity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pl.ticket.event.admin.event.model.AdminEvent;

import java.util.List;

@Slf4j
public class AdminEventIntegrityTest extends PrePost
{
    @Test
    public void testCreateEventRegular2()
    {

        List<AdminEvent> all = adminEventRepository.findAll();

        all.forEach(event -> log.info(event.getTitle()));


    }
}
