package pl.ticket.apigw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig
{
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http)
    {
        http
                .csrf(crsf -> crsf.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/v1/customers/login").permitAll()
                        .pathMatchers("/eureka/**").permitAll()
                        .pathMatchers("/api/v1/customers/register").permitAll()
                        .pathMatchers("/api/v1/customers/**").hasRole("user")
                        .pathMatchers("/api/v1/bookings/**").authenticated()
                        .pathMatchers("/api/v1/events/**").authenticated()
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter()
    {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

}
