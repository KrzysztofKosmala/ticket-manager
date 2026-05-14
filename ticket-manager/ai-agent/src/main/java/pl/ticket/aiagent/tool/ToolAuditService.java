package pl.ticket.aiagent.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

@Slf4j
@Service
public class ToolAuditService {

    private final ObjectMapper objectMapper;

    public ToolAuditService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void success(CallerContext callerContext, ToolContract contract, Object args, Duration duration) {
        log.info("ai_tool_call status=success subject={} tool={} mode={} argsHash={} durationMs={}",
                callerContext.subject(),
                contract.name(),
                contract.accessMode(),
                hashArgs(args),
                duration.toMillis());
    }

    public void failure(CallerContext callerContext, ToolContract contract, Object args, Duration duration, Exception exception) {
        log.warn("ai_tool_call status=failure subject={} tool={} mode={} argsHash={} durationMs={} error={}",
                callerContext.subject(),
                contract.name(),
                contract.accessMode(),
                hashArgs(args),
                duration.toMillis(),
                exception.getClass().getSimpleName());
    }

    private String hashArgs(Object args) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(args);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(json));
        } catch (JsonProcessingException | NoSuchAlgorithmException ex) {
            return "unavailable";
        }
    }
}
