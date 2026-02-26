package pl.ticket.aiagent;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class AuthFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
            String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorization);
            }
        }
    }
}