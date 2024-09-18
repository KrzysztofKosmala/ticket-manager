package pl.ticket.cart.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final String[] swaggerApis = {"/swagger-ui.html",
            "/swagger-resources/**",
            "/swagger-resources/configuration/ui/**",
            "/swagger-resources/configuration/security/**",
            "/v2/api-docs/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**"};

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authorize -> {
                            authorize
                                    .requestMatchers("/api/v1/customers/login").permitAll()
                                    .requestMatchers("/api/v1/customers/register").permitAll()
                                    .requestMatchers(swaggerApis).permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

