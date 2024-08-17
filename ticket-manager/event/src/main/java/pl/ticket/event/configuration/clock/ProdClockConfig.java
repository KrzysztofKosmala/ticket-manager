package pl.ticket.event.configuration.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;

@Configuration
@Profile("prod")
public class ProdClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
