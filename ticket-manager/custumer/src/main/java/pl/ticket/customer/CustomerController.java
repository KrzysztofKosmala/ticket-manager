package pl.ticket.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.ticket.customer.security.UserDetailResponse;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public record CustomerController(CustomerService customerService)
{
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest)
    {
        log.info("Customer registered {}", customerRegistrationRequest);
        customerService.registerCustomer(customerRegistrationRequest);
    }

    @GetMapping
    public UserDetailResponse me(@AuthenticationPrincipal Jwt jwt)
    {
        String email = jwt.getClaim("email");
        String username = jwt.getClaim("preferred_username");
        Map<String, Object> claims = jwt.getClaims();
        return new UserDetailResponse(email, username, claims);
    }
}
