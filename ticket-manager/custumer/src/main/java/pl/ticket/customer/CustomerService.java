package pl.ticket.customer;


import jakarta.ws.rs.BadRequestException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pl.ticket.customer.security.KeycloackSecurityUtils;
import pl.ticket.feign.notification.EmailMessage;
import pl.ticket.amqp.RabbitMqMessageProducer;

import java.util.Collections;

@Service
public class CustomerService
{
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    KeycloackSecurityUtils keycloackSecurityUtils;
    @Autowired
    RabbitMqMessageProducer rabbitMqMessageProducer;

    @Value("${realm}")
    String realm;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest)
    {
        UserRepresentation user = maptoUserRepresentation(customerRegistrationRequest);

        keycloackSecurityUtils.getKeycloakInstance().realm(realm).users().create(user);

        EmailMessage confirmationMessage = new EmailMessage();
        confirmationMessage.setSubject(customerRegistrationRequest.firstName() + " please confirm your email");
        confirmationMessage.setBody("here should be link with generated url (klick to confirm your email)");
        confirmationMessage.setTo(customerRegistrationRequest.email());

//        //todo: przenieść te argumenty do jakiegos ogolnego miejsca
//        rabbitMqMessageProducer.publish(confirmationMessage, "internal.exchange", "internal.accountConfirmation.routing-key");

        int i =0;

    }

    private static UserRepresentation maptoUserRepresentation(CustomerRegistrationRequest customerRegistrationRequest)
    {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(customerRegistrationRequest.firstName());
        user.setEmail(customerRegistrationRequest.email());
        user.setEmailVerified(true);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(/*passwordEncoder.encode(customerRegistrationRequest.password())*/ customerRegistrationRequest.password());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));
        return user;
    }



    public HttpHeaders getRequestHeaderBearer(String token) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " +
                token);

        return headers;
    }
    public ResponseEntity<AccessTokenResponse> loginCustomer(LoginRequest loginRequest) {
        Keycloak keycloakUser = keycloackSecurityUtils.loadKeycloakUser(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
                .build();

        AccessTokenResponse accessToken = null;
        try {
            accessToken = keycloakUser.tokenManager().getAccessToken();
            HttpHeaders authorizationHeader = getRequestHeaderBearer(accessToken.getToken());
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(authorizationHeader)
                    .body(accessToken);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(accessToken);
        }
    }
}
