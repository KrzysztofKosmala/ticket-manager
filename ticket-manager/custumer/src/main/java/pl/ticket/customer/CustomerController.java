package pl.ticket.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public record CustomerController(CustomerService customerService)
{
    @PostMapping("/register")
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest)
    {
        log.info("Customer registered {}", customerRegistrationRequest);
        customerService.registerCustomer(customerRegistrationRequest);
    }

    @GetMapping("/email")
    public String getUserEmail(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaimAsString("email");
    }
}
