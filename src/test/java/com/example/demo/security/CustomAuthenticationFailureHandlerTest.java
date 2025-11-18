package com.example.demo.security;

import com.example.demo.service.ApplicationShutdownService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFailureHandlerTest {

    @Mock
    private ApplicationShutdownService shutdownService;

    private CustomAuthenticationFailureHandler handler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException exception;

    @BeforeEach
    void setUp() {
        handler = new CustomAuthenticationFailureHandler(shutdownService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        exception = new BadCredentialsException("Bad credentials");
        request.setParameter("username", "test-user");
    }

    @Test
    void onAuthenticationFailure_incrementsAttemptsAndReturnsUnauthorized() throws IOException {
        // First attempt
        handler.onAuthenticationFailure(request, response, exception);
        assertEquals(1, handler.getAttempts("test-user"));
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("2 attempts remaining"));

        // Second attempt
        response = new MockHttpServletResponse();
        handler.onAuthenticationFailure(request, response, exception);
        assertEquals(2, handler.getAttempts("test-user"));
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("1 attempts remaining"));

        verifyNoInteractions(shutdownService);
    }

    @Test
    void onAuthenticationFailure_triggersShutdownAfterThreeAttempts() throws IOException {
        // Fail twice
        handler.onAuthenticationFailure(request, new MockHttpServletResponse(), exception);
        handler.onAuthenticationFailure(request, new MockHttpServletResponse(), exception);

        verifyNoInteractions(shutdownService);

        // Third attempt
        handler.onAuthenticationFailure(request, response, exception);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Application is shutting down"));

        verify(shutdownService, times(1)).shutdown();
    }
}
