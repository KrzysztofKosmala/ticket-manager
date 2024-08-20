package pl.ticket.event.configuration.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
@Profile("test")
public class TestClockConfig {

    @Bean
    public Clock clock() {
        return Clock.fixed(Instant.parse("2024-08-15T00:00:00Z"), ZoneId.of("UTC"));
    }
}
