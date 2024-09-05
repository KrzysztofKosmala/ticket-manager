package pl.ticket.event.admin.integrity.event;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.ticket.event.admin.event.repository.AdminEventRepository;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts={"classpath:test-data.sql"})
public class PrePost
{

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.4")
            .withDatabaseName("test")
            .withPassword("test")
            .withUsername("test")
            .withConnectTimeoutSeconds(60);

    @DynamicPropertySource
    public static void containerConfig(DynamicPropertyRegistry registry)
    {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }
}
