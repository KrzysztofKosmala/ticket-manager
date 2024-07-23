package pl.ticket.apigw.security;

public interface ApiKeyAuthorizationChecker
{
    boolean isAuthorized(String key, String application);// to enum
}
