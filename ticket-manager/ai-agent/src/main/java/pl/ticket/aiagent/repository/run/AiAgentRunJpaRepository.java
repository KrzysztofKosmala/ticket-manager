package pl.ticket.aiagent.repository.run;

import pl.ticket.aiagent.model.run.AiAgentRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAgentRunJpaRepository extends JpaRepository<AiAgentRunEntity, String> {
}
