package com.example.demo.security;

import com.example.demo.service.ApplicationShutdownService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);
    private static final int MAX_ATTEMPTS = 3;
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ApplicationShutdownService shutdownService;

    public CustomAuthenticationFailureHandler(ApplicationShutdownService shutdownService) {
        this.shutdownService = shutdownService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String username = request.getParameter("username");
        if (username == null) {
            username = "UNKNOWN";
        }

        logger.warn("Authentication failed for user: {}. Reason: {}", username, exception.getMessage());

        attemptsCache.merge(username, 1, Integer::sum);
        int attempts = getAttempts(username);

        if (attempts >= MAX_ATTEMPTS) {
            logger.error("User {} has reached maximum number of failed login attempts ({}). Shutting down application.", username, MAX_ATTEMPTS);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Too many failed login attempts. Application is shutting down.\"}");
            response.getWriter().flush();
            shutdownService.shutdown();
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Invalid credentials. " + (MAX_ATTEMPTS - attempts) + " attempts remaining.\"}");
            response.getWriter().flush();
        }
    }

    public int getAttempts(String username) {
        return attemptsCache.getOrDefault(username, 0);
    }

    public void resetAttempts() {
        attemptsCache.clear();
    }
}
