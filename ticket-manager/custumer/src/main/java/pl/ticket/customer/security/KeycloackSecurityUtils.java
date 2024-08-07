package pl.ticket.customer.security;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloackSecurityUtils
{

    Keycloak keycloak;

    @Value("${server-url}")
    private String serverUrl;

    @Value("${realm}")
    private String realm;

    @Value("${client-id}")
    private String clientId;

    @Value("${grand-type}")
    private String grantType;
    @Value("${admin-client-secret}")
    private String adminClientSecret;
    @Value("${name}")
    private String name;

    @Value("${password}")
    private String password;

    public Keycloak getKeycloakInstance()
    {
        if(keycloak == null)
        {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .username("test")
                    .password(password)
                    .clientSecret(adminClientSecret)
                    .grantType(grantType)
                    .build();
        }
        return keycloak;
    }
    public KeycloakBuilder loadKeycloakUser(String username, String password) {
        return KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverUrl)
                .clientId(clientId)
                .clientSecret(adminClientSecret)
                .username(username)
                .password(password);
    }
}
