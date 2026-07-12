package pl.ticket.aiagent.conversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiConversationJpaRepository extends JpaRepository<AiConversationEntity, String> {

    Optional<AiConversationEntity> findByIdAndOwnerSubject(String id, String ownerSubject);

    List<AiConversationEntity> findAllByOwnerSubject(String ownerSubject);
}
