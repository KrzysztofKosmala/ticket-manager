package pl.ticket.aitoolsgateway.service;

import org.springframework.stereotype.Service;

@Service
public class SmokeToolService {

    public AiToolsGatewayService.SmokeEchoResponse ping() {
        return new AiToolsGatewayService.SmokeEchoResponse(
                "smoke",
                "SMOKE_OK",
                "ai-tools-gateway"
        );
    }
}
