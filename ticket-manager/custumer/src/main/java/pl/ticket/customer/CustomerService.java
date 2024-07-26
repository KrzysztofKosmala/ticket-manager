package pl.ticket.customer;


import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pl.ticket.customer.security.KeycloackSecurityUtils;

import java.util.Collections;

@Service
public class CustomerService
{
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    KeycloackSecurityUtils keycloackSecurityUtils;

    @Value("${realm}")
    String realm;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest)
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

        keycloackSecurityUtils.getKeycloakInstance().realm(realm).users().create(user);
    }
}
