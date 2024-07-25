package pl.ticket.customer.security;

import java.util.Map;

public class UserDetailResponse {
    private String email;
    private String username;
    private Map<String, Object> claims;

    public UserDetailResponse(String email, String username, Map<String, Object> claims) {
        this.email = email;
        this.username = username;
        this.claims = claims;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }
}
