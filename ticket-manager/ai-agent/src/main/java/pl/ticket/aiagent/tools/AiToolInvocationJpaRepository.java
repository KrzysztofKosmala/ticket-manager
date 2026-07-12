package pl.ticket.aiagent.tools;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiToolInvocationJpaRepository extends JpaRepository<AiToolInvocationEntity, Long> {

    List<AiToolInvocationEntity> findAllByConversationIdOrderByIdAsc(String conversationId);
}
